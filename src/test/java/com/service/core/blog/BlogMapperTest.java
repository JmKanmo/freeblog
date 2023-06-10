package com.service.core.blog;

import com.service.core.blog.dto.BlogMapperDto;
import com.service.core.blog.repository.mapper.BlogMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(readOnly = true)
public class BlogMapperTest {
    @Autowired
    private BlogMapper blogMapper;

    @Test
    public void blogMapperByBlogIdTest() {
        BlogMapperDto blogMapperDto = blogMapper.findBlogMapperDtoByBlogId(4L);
        Assertions.assertNotNull(blogMapperDto);
    }

    @Test
    public void blogMapperByUserIdTest() {
        BlogMapperDto blogMapperDto = blogMapper.findBlogMapperDtoByUserId("nebiros");
        Assertions.assertNotNull(blogMapperDto);
    }

    @Test
    public void blogMapperByEmailTest() {
        BlogMapperDto blogMapperDto = blogMapper.findBlogMapperDtoByEmail("nebi25@naver.com");
        Assertions.assertNotNull(blogMapperDto);
    }
}
