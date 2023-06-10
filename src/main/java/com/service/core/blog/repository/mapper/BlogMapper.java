package com.service.core.blog.repository.mapper;

import com.service.core.blog.dto.BlogDeleteMapperDto;
import com.service.core.blog.dto.BlogMapperDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogMapper {
    BlogMapperDto findBlogMapperDtoByBlogId(Long blogId);

    BlogMapperDto findBlogMapperDtoByUserId(String userId);

    BlogMapperDto findBlogMapperDtoByEmail(String email);

    BlogDeleteMapperDto findBlogDeleteMapperDtoByBlogId(Long blogId);

    BlogDeleteMapperDto findBlogDeleteMapperDtoByUserId(String userId);

    BlogDeleteMapperDto findBlogDeleteMapperDtoByEmail(String email);

    BlogDeleteMapperDto findBlogDeleteMapperDtoByCategoryId(Long categoryId);
}
