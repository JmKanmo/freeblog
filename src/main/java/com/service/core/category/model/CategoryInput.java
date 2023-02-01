package com.service.core.category.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryInput {
    private final Long id;
    private final Long seq;
    private final String name;
    private final String type; // parent | child
}
