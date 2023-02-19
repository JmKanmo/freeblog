package com.service.core.category.service;

import com.service.core.blog.domain.Blog;
import com.service.core.category.domain.Category;
import com.service.core.category.dto.CategoryDto;
import com.service.core.category.model.CategoryInput;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.paging.PostPaginationResponse;
import com.service.core.post.paging.PostSearchPagingDto;

import java.util.List;

public interface CategoryService {
    CategoryDto findCategoryDtoByUserId(String userId);

    CategoryDto findCategoryDtoByBlogId(Long blogId);

    PostTotalDto findPostByCategoryId(Long categoryId);

    PostPaginationResponse<PostTotalDto> findPaginationPostByCategoryId(Long categoryId, PostSearchPagingDto postSearchPagingDto);

    PostPaginationResponse<PostTotalDto> findPaginationPostByBlogId(Long blogId, PostSearchPagingDto postSearchPagingDto);

    String findCategoryName(Category category);

    Category findCategoryById(String email, Long categoryId);

    Category findCategoryById(Long categoryId);

    Category registerBasicCategory(Blog blog);

    void registerCategory(Long blogId, List<CategoryInput> categoryInputList);
}
