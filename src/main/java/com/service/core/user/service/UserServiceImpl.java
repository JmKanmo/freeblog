package com.service.core.user.service;

import com.service.config.UserAuthValidTimeConfig;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.User;
import com.service.core.user.dto.UserDto;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import com.service.core.user.model.UserStatus;
import com.service.core.user.repository.UserRepository;
import com.service.core.user.repository.mapper.UserMapper;
import com.service.util.ConstUtil;
import com.service.util.JmUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 추후에 Redis 도입 후, 비즈니스 로직 개선
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserAuthValidTimeConfig userAuthValidTimeConfig;

    @Transactional
    @Override
    public boolean register(User user) {
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean checkIsActive(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.UserAuthMessage.USER_INFO_NOT_FOUND));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserAuthException(ConstUtil.UserAuthMessage.NOT_AUTHENTICATED_USER);
        }

        return true;
    }

    @Override
    public boolean checkSameUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserManageException(ConstUtil.UserAuthMessage.ALREADY_SAME_EMAIL);
        }
        if (userRepository.findById(user.getUserId()).isPresent()) {
            throw new UserManageException(ConstUtil.UserAuthMessage.ALREADY_SAME_ID);
        }

        return true;
    }

    @Override
    public boolean checkSameId(String id) {
        if (userRepository.findById(id).isPresent()) {
            throw new UserManageException(ConstUtil.UserAuthMessage.ALREADY_SAME_ID);
        }
        return true;
    }

    @Override
    public boolean checkSameEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserManageException(ConstUtil.UserAuthMessage.ALREADY_SAME_EMAIL);
        }
        return true;
    }

    @Transactional
    @Override
    public void emailAuth(UserAuthInput userAuthInput) {
        User user = userRepository.findByEmail(userAuthInput.getEmail()).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.UserAuthMessage.USER_INFO_NOT_FOUND));

        if (!user.getAuthKey().equals(userAuthInput.getKey())) {
            throw new UserAuthException(ConstUtil.UserAuthMessage.AUTH_VALID_KEY_MISMATCH);
        } else if (ChronoUnit.HOURS.between(LocalDateTime.now(), user.getAuthExpireDateTime()) > userAuthValidTimeConfig.getEmailAuthValidTime()) {
            throw new UserAuthException(ConstUtil.UserAuthMessage.AUTH_VALID_TIME_EXPIRED);
        } else if (user.isAuth()) {
            throw new UserAuthException(ConstUtil.UserAuthMessage.ALREADY_AUTHENTICATED_USER);
        }

        user.setAuthKey(JmUtil.createRandomString(20));
        user.setStatus(UserStatus.ACTIVE);
        user.setAuth(true);
        userRepository.save(user);
    }

    @Override
    public List<UserEmailFindDto> findUsersByNickname(String nickname) {
        List<UserEmailFindDto> userEmailFindDtoList = userMapper.findUsersByNickName(nickname);
        userEmailFindDtoList.forEach(userEmailFindDto -> {
            userEmailFindDto.setEmail(JmUtil.encryptEmail(userEmailFindDto.getEmail()));
        });
        return userEmailFindDtoList;
    }

    @Override
    public UserDto findUserByEmail(String email) {
        return UserDto.fromEntity(userRepository.findByEmail(email).orElse(null));
    }

    @Transactional
    @Override
    public String updateEmailAuthCondition(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.UserAuthMessage.USER_INFO_NOT_FOUND));
        String key = JmUtil.createRandomAlphaNumberString(20);
        user.setAuthKey(key);
        user.setAuthExpireDateTime(LocalDateTime.now());
        userRepository.save(user);
        return key;
    }

    @Transactional
    @Override
    public String updatePasswordAuthCondition(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.UserAuthMessage.USER_INFO_NOT_FOUND));
        String key = JmUtil.createRandomAlphaNumberString(20);
        user.setUpdatePasswordExpireDateTime(LocalDateTime.now());
        user.setUpdatePasswordKey(key);
        userRepository.save(user);
        return key;
    }

    @Transactional
    @Override
    public void updatePassword(UserPasswordInput userPasswordInput) {
        User user = userRepository.findByEmail(userPasswordInput.getEmail()).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.UserAuthMessage.USER_INFO_NOT_FOUND));

        if (!user.getUpdatePasswordKey().equals(userPasswordInput.getKey())) {
            throw new UserAuthException(ConstUtil.UserAuthMessage.AUTH_VALID_KEY_MISMATCH);
        } else if (ChronoUnit.HOURS.between(LocalDateTime.now(), user.getUpdatePasswordExpireDateTime()) > userAuthValidTimeConfig.getUpdatePasswordValidTime()) {
            throw new UserAuthException(ConstUtil.UserAuthMessage.AUTH_VALID_TIME_EXPIRED);
        } else if (!userPasswordInput.getPassword().equals(userPasswordInput.getRePassword())) {
            throw new UserAuthException(ConstUtil.UserAuthMessage.RE_PASSWORD_MISMATCH);
        }

        user.setUpdatePasswordKey(JmUtil.createRandomString(20));
        user.setPassword(BCrypt.hashpw(userPasswordInput.getPassword(), BCrypt.gensalt()));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.UserAuthMessage.USER_INFO_NOT_FOUND));

        JmUtil.checkUserStatus(user.getStatus());

        List<GrantedAuthority> grantedAuthorityList = new LinkedList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorityList);
    }
}
