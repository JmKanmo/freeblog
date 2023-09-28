package com.service.core.music.service;

import com.service.core.music.dto.MusicCategoryDto;
import com.service.core.music.repository.mapper.MusicCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicCategoryService {
    private final MusicCategoryMapper musicCategoryMapper;

    public List<MusicCategoryDto> searchMusicCategoryDto() {
        List<MusicCategoryDto> musicCategoryDtoList = musicCategoryMapper.searchMusicCategoryDto();
        return musicCategoryDtoList;
    }
}
