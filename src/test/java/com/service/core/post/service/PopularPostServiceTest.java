package com.service.core.post.service;

import com.service.core.post.dto.PostCardDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PopularPostServiceTest {
    @Autowired
    private PostPopularService postPopularService;

    @Test
    public void popularPostServiceTest() {
        List<PostCardDto> postCardDtoList = postPopularService.findPopularPost(3L);
        Assertions.assertNotNull(postCardDtoList);
    }
}
