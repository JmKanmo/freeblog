package com.service.core.blog.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogInfoDto;

public interface BlogService {
    Blog register(Blog blog);

    BlogInfoDto findBlogInfoDto(String id);
}
