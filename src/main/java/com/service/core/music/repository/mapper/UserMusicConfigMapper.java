package com.service.core.music.repository.mapper;

import com.service.core.music.dto.UserMusicConfigDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMusicConfigMapper {
    int searchUserMusicConfigCountByBlogId(Long blogId);
    UserMusicConfigDto searchUserMusicConfigByBlogId(Long blogId);
}
