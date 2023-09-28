package com.service.core.music.repository.mapper;

import com.service.core.music.dto.MusicDto;
import com.service.core.music.paging.MusicSearchPagingDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MusicMapper {
    List<MusicDto> searchMusicDto(@Param("musicSearchPagingDto") MusicSearchPagingDto musicSearchPagingDto, Long categoryId);

    int searchMusicCount(@Param("musicSearchPagingDto") MusicSearchPagingDto musicSearchPagingDto, Long categoryId);
}
