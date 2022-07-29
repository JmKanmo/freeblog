package com.service.core.user.repository.mapper;

import com.service.core.user.dto.UserEmailFindDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<UserEmailFindDto> findUsersByNickName(String nickname);
}
