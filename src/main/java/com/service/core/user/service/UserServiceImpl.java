package com.service.core.user.service;

import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.User;
import com.service.core.user.model.UserStatus;
import com.service.core.user.repository.UserRepository;
import com.service.util.ConstUtil;
import com.service.util.JmUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean register(User user) {
        userRepository.save(user);
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkActivate(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.USER_INFO_NOT_FOUND));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserAuthException(ConstUtil.NOT_AUTH_USER);
        }

        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkSameUser(User user) {
        if (userRepository.findById(user.getUserId()).isPresent()) {
            throw new UserManageException(ConstUtil.ALREADY_SAME_USER);
        }
        return true;
    }

    @Override
    public void emailAuth(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.USER_INFO_NOT_FOUND));

        if (user.isAuth()) {
            throw new UserAuthException(ConstUtil.ALREADY_AUTHENTICATED_USER);
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setAuth(true);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.USER_INFO_NOT_FOUND));

        JmUtil.checkUserStatus(user.getStatus());

        List<GrantedAuthority> grantedAuthorityList = new LinkedList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(), grantedAuthorityList);
    }
}
