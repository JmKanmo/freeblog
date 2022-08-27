package com.service.core.category.dto;

import com.service.core.category.domain.CategoryMapperDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDto {
    private ParentCategoryDto parentCategoryDto;
    private List<ChildCategoryDto> childCategoryDtoList = new ArrayList<>();

    public void add(CategoryMapperDto categoryMapperDto, String userId) {
        if (categoryMapperDto.getParentId() == 0) {
            parentCategoryDto = ParentCategoryDto.fromEntity(categoryMapperDto, userId);
        } else {
            childCategoryDtoList.add(ChildCategoryDto.fromEntity(categoryMapperDto, userId));
        }
    }
}
