package com.service.core.blog.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogDeleteMapperDto;
import com.service.core.blog.dto.BlogMapperDto;

public interface BlogInfoService {
    Blog register(Blog blog);

    Blog findBlogByIdOrThrow(Long blogId);

    BlogMapperDto findBlogMapperDtoByIdOrThrow(Long blogId);

    BlogMapperDto findBlogMapperDtoByUserIdOrThrow(String userId);

    BlogMapperDto findBlogMapperDtoByEmailOrThrow(String email);

    BlogDeleteMapperDto findBlogDeleteMapperDtoByIdOrThrow(Long blogId);

    BlogDeleteMapperDto findBlogDeleteMapperDtoByUserIdOrThrow(String userId);

    BlogDeleteMapperDto findBlogDeleteMapperDtoByEmailOrThrow(String email);

    BlogDeleteMapperDto findBlogDeleteMapperDtoByCategoryId(Long categoryId);
}
