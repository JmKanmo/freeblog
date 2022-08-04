package com.service.core.user.service;

import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserEmailFindDto;

import java.util.List;

public interface UserInfoService {
    void saveUserDomain(UserDomain user);

    boolean checkUserDomainByEmail(String email);

    boolean checkUserDomainById(String id);

    UserDomain findUserDomainByEmailOrThrow(String email);

    UserDomain findUserDomainByEmailOrElse(String email, UserDomain userDomain);

    UserDomain findUserDomainByIdOrThrow(String id);

    List<UserEmailFindDto> findUsersByNickName(String nickname);
}
