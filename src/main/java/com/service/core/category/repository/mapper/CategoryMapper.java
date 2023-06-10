package com.service.core.category.repository.mapper;

import com.service.core.category.dto.CategoryBasicMapperDto;
import com.service.core.category.dto.CategoryMapperDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryMapperDto> findCategoriesByUserId(String userId);

    List<CategoryMapperDto> findCategoriesByBlogId(Long blogId);

    CategoryBasicMapperDto findCategoryBasicMapperDtoByCategoryId(Long categoryId);

    CategoryBasicMapperDto findCategoryBasicMapperDtoByCategoryIdAndEmail(Long categoryId, String email);
}
