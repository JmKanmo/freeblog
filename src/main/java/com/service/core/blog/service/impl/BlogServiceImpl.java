package com.service.core.blog.service.impl;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogInfoService;
import com.service.core.blog.service.BlogService;
import com.service.core.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {
    private final BlogInfoService blogInfoService;
    private final UserInfoService userInfoService;

    @Override
    public Blog register(Blog blog) {
        return blogInfoService.register(blog);
    }

    @Transactional(readOnly = true)
    @Override
    public BlogInfoDto findBlogInfoDto(String id) {
        Blog blog = userInfoService.findBlogByIdOrThrow(id);
        return BlogInfoDto.fromEntity(blog);
    }
}
