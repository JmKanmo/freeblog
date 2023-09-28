package com.service.core.music.repository.mapper;

import com.service.core.music.dto.MusicCategoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MusicCategoryMapper {
    List<MusicCategoryDto> searchMusicCategoryDto();
}
