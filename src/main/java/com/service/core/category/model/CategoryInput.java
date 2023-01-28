package com.service.core.category.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryInput {
    private Long parentId;
    private String name;
    private Long seq;
    private String type; // parent | child
}
