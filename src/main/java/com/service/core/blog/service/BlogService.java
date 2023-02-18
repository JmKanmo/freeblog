package com.service.core.blog.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogInfoDto;

public interface BlogService {
    Blog register(Blog blog);

    BlogInfoDto findBlogInfoDtoById(String id);

    BlogInfoDto findBlogInfoDtoByEmail(String email);

    BlogInfoDto findBlogInfoDtoById(Long blogId);

    Blog findBlogByEmail(String email);

    Blog findBlogByIdOrThrow(Long blogId);

    boolean isDeleteOrNotFoundBlog(Long blogId);
}
