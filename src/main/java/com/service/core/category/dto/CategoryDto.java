package com.service.core.category.dto;

import com.service.core.category.domain.CategoryMapperDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private TotalCategoryDto totalCategoryDto;
    private List<SubCategoryDto> subCategoryDtoList;

    public static CategoryDto fromEntity(List<CategoryMapperDto> categoryMapperDtoList) {
        Map<Long, SubCategoryDto> subCategoryDtoMap = new HashMap<>();
        int totalCategoryCount = 0;
        List<SubCategoryDto> subCategoryDtoList = new ArrayList<>();

        for (CategoryMapperDto categoryMapperDto : categoryMapperDtoList) {
            if (!subCategoryDtoMap.containsKey(categoryMapperDto.getSeq().longValue())) {
                SubCategoryDto subCategoryDto = new SubCategoryDto();
                subCategoryDto.add(categoryMapperDto);
                subCategoryDtoMap.put(categoryMapperDto.getSeq().longValue(), subCategoryDto);
            } else {
                SubCategoryDto subCategoryDto = subCategoryDtoMap.get(categoryMapperDto.getSeq().longValue());
                subCategoryDto.add(categoryMapperDto);
            }
        }

        for (Long seq : subCategoryDtoMap.keySet()) {
            SubCategoryDto subCategoryDto = subCategoryDtoMap.get(seq);
            totalCategoryCount += subCategoryDto.getPostCount();
            subCategoryDtoList.add(subCategoryDto);
        }

        return CategoryDto.builder()
                .totalCategoryDto(TotalCategoryDto.fromEntity(totalCategoryCount))
                .subCategoryDtoList(subCategoryDtoList)
                .build();
    }
}
