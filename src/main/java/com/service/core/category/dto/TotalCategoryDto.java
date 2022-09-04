package com.service.core.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalCategoryDto {
    private int totalCount;
    private String link;

    public static TotalCategoryDto fromEntity(int totalCount) {
        return TotalCategoryDto.builder()
                .totalCount(totalCount)
                .link(String.format("/category/post/all"))
                .build();
    }
}
