package com.service.core.category.service;

import com.service.core.category.domain.Category;
import com.service.core.category.dto.CategoryDto;
import com.service.core.post.dto.PostTotalDto;

public interface CategoryService {
    CategoryDto findCategoryDto(String userId);

    PostTotalDto findPostByCategoryId(Long categoryId);

    PostTotalDto findPostByBlogId(Long blogId);

    String findCategoryName(Category category);
}
