package com.service.core.post.repository;

import com.service.core.post.dto.PostLinkDto;
import com.service.core.post.repository.mapper.PostMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PostRepositoryTest {
    @Autowired
    PostMapper postMapper;

    @Test
    public void postMapperTest() {
        List<PostLinkDto> postLinkDtoList = postMapper.findPostLinkDtoList(3L, 0);
        Assertions.assertNotNull(postLinkDtoList);
    }

    @Test
    public void findEqualPostCountTest() {
        int result = postMapper.findEqualPostCount(4L, 37L);
        System.out.println(result);
    }
}
