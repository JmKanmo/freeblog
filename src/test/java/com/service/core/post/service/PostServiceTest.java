package com.service.core.post.service;

import com.service.core.post.dto.PostTotalDto;
import com.service.util.ConstUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PostServiceTest {
    @Autowired
    private PostService postService;

    @Test
    public void findTotalPostTest() {
        PostTotalDto postDtoList = postService.findTotalPost(1L, ConstUtil.TOTAL_POST);
        Assertions.assertNotNull(postDtoList);
    }
}
