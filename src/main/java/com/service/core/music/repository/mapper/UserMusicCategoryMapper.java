package com.service.core.music.repository.mapper;

import com.service.core.music.dto.UserMusicCategoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMusicCategoryMapper {
    List<UserMusicCategoryDto> searchUserMusicCategoryDto();
}
