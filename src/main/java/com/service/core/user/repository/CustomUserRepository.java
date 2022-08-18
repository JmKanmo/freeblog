package com.service.core.user.repository;

import com.service.core.user.dto.UserEmailFindDto;

import java.util.List;

public interface CustomUserRepository {
    List<UserEmailFindDto> findUsersByNickName(String nickname);
}
