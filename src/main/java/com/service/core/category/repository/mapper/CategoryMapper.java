package com.service.core.category.repository.mapper;

import com.service.core.category.domain.CategoryMapperDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryMapperDto> findCategories(String userId);
}
