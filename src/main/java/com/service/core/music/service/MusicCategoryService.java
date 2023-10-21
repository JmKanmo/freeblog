package com.service.core.music.service;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.MusicManageException;
import com.service.core.music.domain.MusicCategory;
import com.service.core.music.dto.MusicCategoryDto;
import com.service.core.music.repository.MusicCategoryRepository;
import com.service.core.music.repository.mapper.MusicCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicCategoryService {
    private final MusicCategoryMapper musicCategoryMapper;
    private final MusicCategoryRepository musicCategoryRepository;

    public MusicCategory findMusicCategoryById(Long musicCategoryId) {
        return musicCategoryRepository.findById(musicCategoryId).orElseThrow(() -> new MusicManageException(ServiceExceptionMessage.MUSIC_CATEGORY_NOT_FOUND));
    }

    public MusicCategoryDto findMusicCategoryDtoById(Long musicCategoryId) {
        return MusicCategoryDto.from(musicCategoryRepository.findById(musicCategoryId).orElseThrow(() -> new MusicManageException(ServiceExceptionMessage.MUSIC_CATEGORY_NOT_FOUND)));
    }

    public List<MusicCategoryDto> searchMusicCategoryDto() {
        List<MusicCategoryDto> musicCategoryDtoList = musicCategoryMapper.searchMusicCategoryDto();
        return musicCategoryDtoList;
    }
}
