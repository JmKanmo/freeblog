package com.service.core.music.repository.mapper;

import com.service.core.music.dto.UserMusicDto;
import com.service.core.music.paging.MusicSearchPagingDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMusicMapper {
    List<UserMusicDto> searchUserMusicDto(@Param("musicSearchPagingDto") MusicSearchPagingDto musicSearchPagingDto, Long categoryId, Long blogId);

    int searchUserMusicCount(@Param("musicSearchPagingDto") MusicSearchPagingDto musicSearchPagingDto, Long categoryId, Long blogId);

    int searchUserMusicByHashCode(int hashCode);
}
