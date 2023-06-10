package com.service.core.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMapperDto {
    private Long categoryId;
    private Long parentId;
    private String name;
    private Long seq;
    private Long postCount;
}
