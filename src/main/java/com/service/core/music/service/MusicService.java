package com.service.core.music.service;

import com.service.core.music.dto.MusicDto;
import com.service.core.music.paging.MusicPagination;
import com.service.core.music.paging.MusicPaginationResponse;
import com.service.core.music.paging.MusicSearchPagingDto;
import com.service.core.music.repository.MusicRepository;
import com.service.core.music.repository.mapper.MusicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicService {
    private final MusicMapper musicMapper;
    private final MusicRepository musicRepository;

    public MusicPaginationResponse<List<MusicDto>> searchMusicDto(MusicSearchPagingDto musicSearchPagingDto, long categoryId) {
        int musicCount = musicMapper.searchMusicCount();
        MusicPagination musicPagination = new MusicPagination(musicCount, musicSearchPagingDto);
        musicSearchPagingDto.setMusicPagination(musicPagination);
        return new MusicPaginationResponse<>(musicMapper.searchMusicDto(musicSearchPagingDto, categoryId), musicPagination);
    }
}
