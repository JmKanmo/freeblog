package com.service.core.music.service;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.MusicManageException;
import com.service.core.music.domain.UserMusicCategory;
import com.service.core.music.dto.UserMusicCategoryDto;
import com.service.core.music.repository.UserMusicCategoryRepository;
import com.service.core.music.repository.mapper.UserMusicCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMusicCategoryService {
    private final UserMusicCategoryMapper userMusicCategoryMapper;
    private final UserMusicCategoryRepository userMusicCategoryRepository;

    @Transactional
    public UserMusicCategory saveUserMusicCategory(UserMusicCategory userMusicCategory) {
        return userMusicCategoryRepository.save(userMusicCategory);
    }

    public boolean isExistUserMusicCategoryByTargetId(Long targetId) {
        return userMusicCategoryRepository.existsByTargetId(targetId);
    }

    public boolean isExistUserMusicCategoryByCategoryId(Long categoryId) {
        return userMusicCategoryRepository.existsById(categoryId);
    }

    public List<UserMusicCategoryDto> searchUserMusicCategoryDto(Long blogId) {
        return userMusicCategoryMapper.searchUserMusicCategoryDto(blogId);
    }

    public UserMusicCategoryDto findUserMusicCategoryDtoById(Long categoryId) {
        return UserMusicCategoryDto.from(userMusicCategoryRepository.findById(categoryId).orElseThrow(() -> new MusicManageException(ServiceExceptionMessage.MUSIC_CATEGORY_NOT_FOUND)));
    }

    public UserMusicCategoryDto findUserMusicCategoryDtoByIdOrElseNull(Long categoryId) {
        UserMusicCategory userMusicCategory = findUserMusicCategoryByIdOrElseNull(categoryId);
        if (userMusicCategory == null) {
            return null;
        } else {
            return UserMusicCategoryDto.from(userMusicCategory);
        }
    }

    public UserMusicCategory findUserMusicCategoryByIdOrElseThrow(Long categoryId) {
        return userMusicCategoryRepository.findById(categoryId).orElseThrow(() -> new MusicManageException(ServiceExceptionMessage.MUSIC_CATEGORY_NOT_FOUND));
    }

    public UserMusicCategory findUserMusicCategoryByIdOrElseNull(Long categoryId) {
        return userMusicCategoryRepository.findById(categoryId).orElse(null);
    }

    public UserMusicCategory findUserMusicCategoryByTargetIdAndBlogId(Long targetId, Long blogId) {
        return userMusicCategoryRepository.findByTargetIdAndBlogId(targetId, blogId);
    }
}
