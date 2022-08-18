package com.service.core.user.service.impl;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.repository.CustomUserRepository;
import com.service.core.user.repository.UserRepository;
import com.service.core.user.repository.mapper.UserMapper;
import com.service.core.user.service.UserInfoService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInfoServiceImpl implements UserInfoService {
    private final UserRepository userRepository;
    private final CustomUserRepository customUserRepository;

    //    private final UserMapper userMapper;

    @Transactional
    @Override
    public void saveUserDomain(UserDomain user) {
        userRepository.save(user);
    }

    @Override
    public boolean checkUserDomainByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean checkUserDomainById(String id) {
        return userRepository.findById(id).isPresent();
    }

    @Override
    public UserDomain findUserDomainByEmailOrThrow(String email) {
        UserDomain userDomain = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(ServiceExceptionMessage.ACCOUNT_INFO_NOT_FOUND.message()));
        BlogUtil.checkUserStatus(userDomain.getStatus());
        return userDomain;
    }

    @Override
    public UserDomain findUserDomainByEmailOrElse(String email, UserDomain defaultValue) {
        return userRepository.findByEmail(email).orElse(defaultValue);
    }

    @Override
    public UserDomain findUserDomainByIdOrThrow(String id) {
        UserDomain userDomain = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(ServiceExceptionMessage.ACCOUNT_INFO_NOT_FOUND.message()));
        BlogUtil.checkUserStatus(userDomain.getStatus());
        return userDomain;
    }

    @Override
    public List<UserEmailFindDto> findUsersByNickName(String nickname) {
        return customUserRepository.findUsersByNickName(nickname);
    }
}
