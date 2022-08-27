package com.service.core.category.service;

import com.service.core.category.dto.CategoryDto;

public interface CategoryService {
    CategoryDto findCategoryDto(String userId);
}
