package com.service.core.user.service;

import com.service.core.user.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    boolean register(User user);

    boolean emailAuth(String uuid);
}
