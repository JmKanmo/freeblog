package com.service.core.user.service;

import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.User;
import com.service.core.user.dto.UserDto;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public boolean register(User user) {
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean checkActivate(String email) {
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

    @Override
    public void emailAuth(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.UserAuthMessage.USER_INFO_NOT_FOUND));

        if (user.isAuth()) {
            throw new UserAuthException(ConstUtil.UserAuthMessage.ALREADY_AUTHENTICATED_USER);
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setAuth(true);
        userRepository.save(user);
    }

    @Override
    public UserDto findUserByEmail(String email) {
        return UserDto.fromEntity(userRepository.findByEmail(email).orElse(null));
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
