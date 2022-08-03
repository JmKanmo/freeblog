package com.service.core.user.service.impl;

import com.service.config.UserAuthValidTimeConfig;
import com.service.core.error.model.UserAuthException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.domain.UserEmailAuth;
import com.service.core.user.domain.UserPasswordAuth;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import com.service.core.user.repository.UserEmailAuthRepository;
import com.service.core.user.repository.UserPasswordAuthRepository;
import com.service.core.user.service.UserAuthService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * @TimeToLive 어노테이션 동작에 따른
 * Redis caching 여부에 따라 try-catch 로직 추가할지 결정 ...
 * redis 내에서 자연스럽게 캐싱 되서 존재하지 않을 시에 예외 던지기
 */
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserEmailAuthRepository userEmailAuthRepository;
    private final UserPasswordAuthRepository userPasswordAuthRepository;
    private final UserAuthValidTimeConfig userAuthValidTimeConfig;

    @Override
    public String saveUserEmailAuth(int id) {
        if (userEmailAuthRepository.existsById(id)) {
            UserEmailAuth userEmailAuth = userEmailAuthRepository.findById(id).get();
            userEmailAuth.setEmailAuthKey(BlogUtil.createRandomAlphaNumberString(20));
            userEmailAuth.setEmailAuthExpireDateTime(LocalDateTime.now());
            return userEmailAuthRepository.save(userEmailAuth).getEmailAuthKey();
        } else {
            return userEmailAuthRepository.save(UserEmailAuth.from(id)).getEmailAuthKey();
        }
    }

    @Override
    public String saveUserPasswordAuth(int id) {
        if (userPasswordAuthRepository.existsById(id)) {
            UserPasswordAuth userPasswordAuth = userPasswordAuthRepository.findById(id).get();
            userPasswordAuth.setUpdatePasswordAuthKey(BlogUtil.createRandomAlphaNumberString(20));
            userPasswordAuth.setUpdatePasswordExpireDateTime(LocalDateTime.now());
            return userPasswordAuthRepository.save(userPasswordAuth).getUpdatePasswordAuthKey();
        } else {
            return userPasswordAuthRepository.save(UserPasswordAuth.from(id)).getUpdatePasswordAuthKey();
        }
    }

    @Override
    public String findUserEmailAuthKey(int id) {
        UserEmailAuth userEmailAuth = userEmailAuthRepository.findById(id).orElseThrow(() -> new UserAuthException(ConstUtil.ExceptionMessage.AUTH_KEY_NOT_FOUND));
        return userEmailAuth.getEmailAuthKey();
    }

    @Override
    public boolean checkUserEmailAuth(UserDomain userDomain, UserAuthInput userAuthInput) {
        int id = BlogUtil.createUserAuthId(userDomain);
        UserEmailAuth userEmailAuth = userEmailAuthRepository.findById(id).orElseThrow(() -> new UserAuthException(ConstUtil.ExceptionMessage.AUTH_KEY_NOT_FOUND));

        if (Objects.isNull(userEmailAuth.getEmailAuthKey()) || !userEmailAuth.getEmailAuthKey().equals(userAuthInput.getKey())) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.AUTH_VALID_KEY_MISMATCH);
        } else if (Objects.isNull(userEmailAuth.getEmailAuthExpireDateTime()) || ChronoUnit.HOURS.between(LocalDateTime.now(), userEmailAuth.getEmailAuthExpireDateTime()) > userAuthValidTimeConfig.getEmailAuthValidTime()) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.AUTH_VALID_TIME_EXPIRED);
        } else if (userDomain.isAuth()) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.ALREADY_AUTHENTICATED_USER);
        }
        userEmailAuthRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean checkUserPasswordAuth(UserDomain userDomain, UserPasswordInput userPasswordInput) {
        int id = BlogUtil.createUserAuthId(userDomain);
        UserPasswordAuth userPasswordAuth = userPasswordAuthRepository.findById(id).orElseThrow(() -> new UserAuthException(ConstUtil.ExceptionMessage.AUTH_KEY_NOT_FOUND));

        if (Objects.isNull(userPasswordAuth.getUpdatePasswordAuthKey()) || !userPasswordAuth.getUpdatePasswordAuthKey().equals(userPasswordInput.getKey())) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.AUTH_VALID_KEY_MISMATCH);
        } else if (Objects.isNull(userPasswordAuth.getUpdatePasswordExpireDateTime()) || ChronoUnit.HOURS.between(LocalDateTime.now(), userPasswordAuth.getUpdatePasswordExpireDateTime()) > userAuthValidTimeConfig.getUpdatePasswordValidTime()) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.AUTH_VALID_TIME_EXPIRED);
        } else if (!userPasswordInput.getPassword().equals(userPasswordInput.getRePassword())) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.RE_PASSWORD_MISMATCH);
        }
        userPasswordAuthRepository.deleteById(id);
        return true;
    }
}
