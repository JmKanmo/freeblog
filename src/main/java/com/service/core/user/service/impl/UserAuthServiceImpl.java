package com.service.core.user.service.impl;

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

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    private final UserEmailAuthRepository userEmailAuthRepository;
    private final UserPasswordAuthRepository userPasswordAuthRepository;

    @Override
    public String saveUserEmailAuth(int id) {
        if (userEmailAuthRepository.existsById(id)) {
            UserEmailAuth userEmailAuth = userEmailAuthRepository.findById(id).get();
            userEmailAuth.setEmailAuthKey(BlogUtil.createRandomAlphaNumberString(20));
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
        } else if (userDomain.isAuth()) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.ALREADY_AUTHENTICATED_USER);
        }
        userEmailAuth.setEmailAuthKey(BlogUtil.createRandomString(20));
        userEmailAuthRepository.save(userEmailAuth);
        return true;
    }

    @Override
    public boolean checkUserPasswordAuth(UserDomain userDomain, UserPasswordInput userPasswordInput) {
        int id = BlogUtil.createUserAuthId(userDomain);
        UserPasswordAuth userPasswordAuth = userPasswordAuthRepository.findById(id).orElseThrow(() -> new UserAuthException(ConstUtil.ExceptionMessage.AUTH_KEY_NOT_FOUND));

        if (Objects.isNull(userPasswordAuth.getUpdatePasswordAuthKey()) || !userPasswordAuth.getUpdatePasswordAuthKey().equals(userPasswordInput.getKey())) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.AUTH_VALID_KEY_MISMATCH);
        } else if (!userPasswordInput.getPassword().equals(userPasswordInput.getRePassword())) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.RE_PASSWORD_MISMATCH);
        }
        userPasswordAuth.setUpdatePasswordAuthKey(BlogUtil.createRandomString(20));
        userPasswordAuthRepository.save(userPasswordAuth);
        return true;
    }
}
