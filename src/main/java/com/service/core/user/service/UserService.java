package com.service.core.user.service;

import com.service.core.user.domain.User;
import com.service.core.user.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    boolean register(User user);
    boolean checkActivate(String email);
    boolean checkSameUser(User user);

    boolean checkSameId(String id);
    boolean checkSameEmail(String email);
    void emailAuth(String email);

    UserDto findUserByEmail(String email);
}
