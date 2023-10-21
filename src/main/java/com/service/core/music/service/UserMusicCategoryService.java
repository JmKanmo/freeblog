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
    public void saveUserMusicCategory(UserMusicCategory userMusicCategory) {
        userMusicCategoryRepository.save(userMusicCategory);
    }

    public boolean isExistUserMusicCategoryByTargetId(Long targetId) {
        return userMusicCategoryRepository.existsByTargetId(targetId);
    }

    public boolean isExistUserMusicCategoryByCategoryId(Long categoryId) {
        return userMusicCategoryRepository.existsById(categoryId);
    }

    public List<UserMusicCategoryDto> searchUserMusicCategoryDto() {
        return userMusicCategoryMapper.searchUserMusicCategoryDto();
    }

    public UserMusicCategoryDto findUserMusicCategoryDtoById(Long categoryId) {
        return UserMusicCategoryDto.from(userMusicCategoryRepository.findById(categoryId).orElseThrow(() -> new MusicManageException(ServiceExceptionMessage.MUSIC_CATEGORY_NOT_FOUND)));
    }

    public UserMusicCategory findUserMusicCategoryById(Long categoryId) {
        return userMusicCategoryRepository.findById(categoryId).orElseThrow(() -> new MusicManageException(ServiceExceptionMessage.MUSIC_CATEGORY_NOT_FOUND));
    }
}
