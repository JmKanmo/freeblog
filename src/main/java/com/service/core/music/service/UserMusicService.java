package com.service.core.music.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMusicService {
    private final BlogService blogService;
    private final MusicCategoryService musicCategoryService;
    private final UserMusicCategoryService userMusicCategoryService;
    private final UserMusicMapper userMusicMapper;
    private final UserMusicRepository userMusicRepository;

    @Transactional(readOnly = true)
    public MusicPaginationResponse<List<UserMusicDto>> searchUserMusicDto(MusicSearchPagingDto musicSearchPagingDto, long categoryId) {
        int userMusicCount = userMusicMapper.searchUserMusicCount(musicSearchPagingDto, categoryId);
        MusicPagination musicPagination = new MusicPagination(userMusicCount, musicSearchPagingDto);
        musicSearchPagingDto.setMusicPagination(musicPagination);
        return new MusicPaginationResponse<>(userMusicMapper.searchUserMusicDto(musicSearchPagingDto, categoryId), musicPagination);
    }

    @Transactional
    public String downloadMusic(String email, List<UserMusicInput> userMusicInputList) {
        List<UserMusic> userMusicList = new ArrayList<>();
        Blog blog = blogService.findBlogByEmail(email);

        for (UserMusicInput userMusicInput : userMusicInputList) {
            Long categoryId = userMusicInput.getMusicCategoryId();
            MusicCategoryDto musicCategoryDto = musicCategoryService.findMusicCategoryDtoById(categoryId);
            UserMusicCategory userMusicCategory = userMusicCategoryService.findUserMusicCategoryByTargetId(categoryId);

            if (userMusicCategory == null) {
                userMusicCategory = userMusicCategoryService.saveUserMusicCategory(UserMusicCategory.from(
                        musicCategoryDto.getCategoryId(),
                        musicCategoryDto.getName(),
                        blog));
            }
            UserMusic userMusic = UserMusic.from(userMusicInput, userMusicCategory);

            if (findUserMusicByHashCode(userMusic.getHashCode()) == false) {
                userMusicList.add(userMusic);
            }
        }

        if (userMusicList.isEmpty() == false) {
            saveUserMusic(userMusicList);
        }
        return "OK";
    }

    public boolean findUserMusicByHashCode(int hashcode) {
        UserMusic userMusic = userMusicRepository.findByHashCode(hashcode);
        return userMusic == null ? false : true;
    }

    @Transactional
    public void saveUserMusic(List<UserMusic> userMusicList) {
        this.userMusicRepository.saveAll(userMusicList);
    }
}
