package com.service.core.user.service;

import com.service.core.user.domain.User;
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

    @Override
    public boolean emailAuth(String uuid) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(ConstUtil.USER_INFO_NOT_FOUND));

        JmUtil.checkUserStatus(user.getStatus());

        List<GrantedAuthority> grantedAuthorityList = new LinkedList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(), grantedAuthorityList);
    }
}
