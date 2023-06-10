package com.service.core.category.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryBasicMapperDto {
    private final Long id;
    private final String name;
    private final Long parentId;
    private final Long seq;
    private final Long blogId;
    private final Boolean isDelete;
}
