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

    public static TotalCategoryDto fromEntity(String userId, int totalCount) {
        return TotalCategoryDto.builder()
                .totalCount(totalCount)
                .link(String.format("/blog/%s/category/all", userId))
                .build();
    }
}
