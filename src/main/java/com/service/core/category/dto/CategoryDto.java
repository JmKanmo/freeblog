package com.service.core.category.dto;

import com.service.core.category.domain.CategoryMapperDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private TotalCategoryDto totalCategoryDto;
    private List<SubCategoryDto> subCategoryDtoList;

    public static CategoryDto fromEntity(List<CategoryMapperDto> categoryMapperDtoList, String userId) {
        List<SubCategoryDto> subCategoryDtoList = new ArrayList<>();
        int totalCategoryCount = 0;

        for (CategoryMapperDto categoryMapperDto : categoryMapperDtoList) {
            if (subCategoryDtoList.size() < categoryMapperDto.getSeq().intValue()) {
                subCategoryDtoList.add(new SubCategoryDto());
            }
            SubCategoryDto subCategoryDto = subCategoryDtoList.get(categoryMapperDto.getSeq().intValue() - 1);
            subCategoryDto.add(categoryMapperDto, userId);
        }

        for (SubCategoryDto subCategoryDto : subCategoryDtoList) {
            totalCategoryCount += subCategoryDto.getPostCount();
        }

        return CategoryDto.builder()
                .totalCategoryDto(TotalCategoryDto.fromEntity(totalCategoryCount))
                .subCategoryDtoList(subCategoryDtoList)
                .build();
    }
}
