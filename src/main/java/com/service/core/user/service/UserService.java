package com.service.core.user.service;

import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserDto;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    boolean register(UserDomain user);

    boolean checkIsActive(String email);

    boolean checkSameUser(UserDomain user);

    boolean checkSameId(String id);

    boolean checkSameEmail(String email);

    void emailAuth(UserAuthInput userAuthInput);

    List<UserEmailFindDto> findUsersByNickname(String nickname);

    String updateEmailAuthCondition(String email);

    String updatePasswordAuthCondition(String email);

    void updatePassword(UserPasswordInput userPasswordInput);
    UserDto findUserByEmail(String email);
}
