package com.service.core.category.service;

import com.service.core.category.domain.Category;
import com.service.core.category.dto.CategoryDto;
import com.service.core.category.repository.CategoryRepository;
import com.service.core.category.repository.mapper.CategoryMapper;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.service.PostService;
import com.service.util.ConstUtil;
import com.service.util.redis.CacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ArrayUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PostService postService;

    @Cacheable(value = CacheKey.CATEGORY_DTO, key = "#userId")
    @Override
    public CategoryDto findCategoryDto(String userId) {
        return CategoryDto.fromEntity(categoryMapper.findCategories(userId), userId);
    }

    @Override
    public PostTotalDto findPostByCategoryId(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if (categoryOptional.isEmpty()) {
            return PostTotalDto.fromPostDtoList(Collections.emptyList(), ConstUtil.NOT_EXIST_CATEGORY);
        } else {
            Category category = categoryOptional.get();
            return PostTotalDto.fromPostDtoList(category.getPostList().stream().map(PostDto::fromEntity).collect(Collectors.toList()), findCategoryName(category));
        }
    }

    @Override
    public PostTotalDto findPostByBlogId(Long blogId) {
        return postService.findTotalPost(blogId, ConstUtil.TOTAL_CATEGORY);
    }

    @Override
    public String findCategoryName(Category category) {
        Long parentCategoryId = category.getParentId();
        String categoryName = category.getName();

        if (parentCategoryId == 0 || !categoryRepository.existsById(parentCategoryId)) {
            return categoryName;
        } else {
            Category parentCategory = categoryRepository.findById(parentCategoryId).get();
            String parentCategoryName = parentCategory.getName();
            return parentCategoryName + "/" + categoryName;
        }
    }
}
