package com.service.core.music;

import com.service.core.music.dto.MusicDto;
import com.service.core.music.paging.MusicPagination;
import com.service.core.music.paging.MusicSearchPagingDto;
import com.service.core.music.repository.mapper.MusicMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MusicTest {
    @Autowired
    private MusicMapper musicMapper;

    @Test
    public void musicTest() {
        MusicSearchPagingDto musicSearchPagingDto = new MusicSearchPagingDto();
        musicSearchPagingDto.setKeyword("r");
        musicSearchPagingDto.setSearchType("LIKE");
        musicSearchPagingDto.setKeywordType("NAME");
        musicSearchPagingDto.setOrderBy("ASC");
        MusicPagination musicPagination = new MusicPagination(3, musicSearchPagingDto);
        // parameter 값이 -10과 같거나 ... 혹은 더 낮거나 그러면 에러 발생..
        musicSearchPagingDto.setMusicPagination(musicPagination);
        List<MusicDto> musicDtoList = musicMapper.searchMusicDto(musicSearchPagingDto, -255L);
        Assertions.assertNotNull(musicDtoList);
    }
}
