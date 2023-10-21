package com.service.core.music.service;

import com.service.core.blog.domain.Blog;
import com.service.core.music.domain.UserMusic;
import com.service.core.music.domain.UserMusicCategory;
import com.service.core.music.dto.MusicCategoryDto;
import com.service.core.music.dto.UserMusicDto;
import com.service.core.music.model.UserMusicInput;
import com.service.core.music.paging.MusicPagination;
import com.service.core.music.paging.MusicPaginationResponse;
import com.service.core.music.paging.MusicSearchPagingDto;
import com.service.core.music.repository.UserMusicRepository;
import com.service.core.music.repository.mapper.UserMusicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMusicService {
    private final MusicCategoryService musicCategoryService;
    private final UserMusicCategoryService userMusicCategoryService;
    private final UserMusicMapper userMusicMapper;
    private final UserMusicRepository userMusicRepository;

    public MusicPaginationResponse<List<UserMusicDto>> searchUserMusicDto(MusicSearchPagingDto musicSearchPagingDto, long categoryId) {
        int userMusicCount = userMusicMapper.searchUserMusicCount(musicSearchPagingDto, categoryId);
        MusicPagination musicPagination = new MusicPagination(userMusicCount, musicSearchPagingDto);
        musicSearchPagingDto.setMusicPagination(musicPagination);
        return new MusicPaginationResponse<>(userMusicMapper.searchUserMusicDto(musicSearchPagingDto, categoryId), musicPagination);
    }

    @Transactional
    public String downloadMusic(Blog blog, List<UserMusicInput> userMusicInputList) {
        List<UserMusic> userMusicList = new ArrayList<>();

        for (UserMusicInput userMusicInput : userMusicInputList) {
            Long categoryId = userMusicInput.getMusicCategoryId();
            UserMusicCategory userMusicCategory = null;
            MusicCategoryDto musicCategoryDto = musicCategoryService.findMusicCategoryDtoById(categoryId);

            if (userMusicCategoryService.isExistUserMusicCategoryByTargetId(categoryId) == false) {
                userMusicCategory = UserMusicCategory.from(
                        musicCategoryDto.getCategoryId(),
                        musicCategoryDto.getName(),
                        blog);
                userMusicCategoryService.saveUserMusicCategory(userMusicCategory);
            }
            userMusicList.add(UserMusic.from(userMusicInput, userMusicCategory));
        }
        saveUserMusic(userMusicList);
        return "OK";
    }

    @Transactional
    public void saveUserMusic(List<UserMusic> userMusicList) {
        this.userMusicRepository.saveAll(userMusicList);
    }
}
