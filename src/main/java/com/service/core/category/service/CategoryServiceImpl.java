package com.service.core.category.service;

import com.service.core.category.dto.CategoryDto;
import com.service.core.category.repository.CategoryRepository;
import com.service.core.category.repository.mapper.CategoryMapper;
import com.service.util.redis.CacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Cacheable(value = CacheKey.CATEGORY_DTO, key = "#userId")
    @Override
    public CategoryDto findCategoryDto(String userId) {
        return CategoryDto.fromEntity(categoryMapper.findCategories(userId), userId);
    }
}
