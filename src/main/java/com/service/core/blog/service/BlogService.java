package com.service.core.blog.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogDeleteDto;
import com.service.core.blog.dto.BlogInfoDto;

public interface BlogService {
    Blog register(Blog blog);

    BlogInfoDto findBlogInfoDtoById(String id);

    BlogDeleteDto findBlogDeleteDtoById(String id);

    BlogInfoDto findBlogInfoDtoByEmail(String email);

    BlogDeleteDto findBlogDeleteDtoByEmail(String email);

    BlogInfoDto findBlogInfoDtoById(Long blogId);

    BlogDeleteDto findBlogDeleteDtoByBlogId(Long blogId);

    Blog findBlogByEmail(String email);

    Blog findBlogByIdOrThrow(Long blogId);

    boolean isDeleteOrNotFoundBlog(Long blogId);

    void visitBlog(int blogId, int visitorId) throws Exception;
}
