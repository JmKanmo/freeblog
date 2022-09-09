package com.service.core.category.service;

import com.service.core.category.domain.Category;
import com.service.core.category.dto.CategoryDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.util.paging.PaginationResponse;
import com.service.util.paging.SearchDto;

public interface CategoryService {
    CategoryDto findCategoryDtoByUserId(String userId);

    CategoryDto findCategoryDtoByBlogId(Long blogId);

    PostTotalDto findPostByCategoryId(Long categoryId);

    PaginationResponse<PostTotalDto> findPaginationPostByCategoryId(Long categoryId, SearchDto searchDto);

    PostTotalDto findPostByBlogId(Long blogId);

    PaginationResponse<PostTotalDto> findPaginationPostByBlogId(Long blogId, SearchDto searchDto);

    String findCategoryName(Category category);

    Category findCategoryById(String email, Long categoryId);
}
