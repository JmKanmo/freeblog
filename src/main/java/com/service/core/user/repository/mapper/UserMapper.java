package com.service.core.user.repository.mapper;

import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.dto.UserProfileMapperDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    UserProfileMapperDto findUserProfileMapperDtoByBlogId(Long blogId);
}
