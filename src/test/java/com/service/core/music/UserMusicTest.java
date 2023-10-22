package com.service.core.music;

import com.service.core.music.dto.MusicCategoryDto;
import com.service.core.music.dto.MusicDto;
import com.service.core.music.dto.UserMusicCategoryDto;
import com.service.core.music.dto.UserMusicDto;
import com.service.core.music.paging.MusicPagination;
import com.service.core.music.paging.MusicSearchPagingDto;
import com.service.core.music.repository.mapper.UserMusicCategoryMapper;
import com.service.core.music.repository.mapper.UserMusicMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserMusicTest {
    @Autowired
    private UserMusicMapper userMusicMapper;
    @Autowired
    private UserMusicCategoryMapper userMusicCategoryMapper;

    @Test
    public void userMusicCategoryTest() {
        List<UserMusicCategoryDto> s = userMusicCategoryMapper.searchUserMusicCategoryDto(3L);
        Assertions.assertNotNull(s);
    }

    @Test
    public void userMusicTest() {
        MusicSearchPagingDto musicSearchPagingDto = new MusicSearchPagingDto();
        musicSearchPagingDto.setKeyword("");
        musicSearchPagingDto.setSearchType("LIKE");
        musicSearchPagingDto.setKeywordType("ALL");
        musicSearchPagingDto.setOrderBy("ASC");
        MusicPagination musicPagination = new MusicPagination(7, musicSearchPagingDto);
        // parameter 값이 -10과 같거나 ... 혹은 더 낮거나 그러면 에러 발생..
        musicSearchPagingDto.setMusicPagination(musicPagination);
        List<UserMusicDto> musicDtoList = userMusicMapper.searchUserMusicDto(musicSearchPagingDto, 1L);
        Assertions.assertNotNull(musicDtoList);
    }

    @Test
    public void hastCodeTest() {
        int a = "헤이즈(HEIZE,호텔 델루나OST) - 내맘을볼수잇나요".hashCode();
        int b = "헤이즈(HEIZE,호텔 델루나OST) - 내맘을볼수잇나요".hashCode();
        System.out.println(a == b);
    }
}
