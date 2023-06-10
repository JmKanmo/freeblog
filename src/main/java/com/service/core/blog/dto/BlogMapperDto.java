package com.service.core.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogMapperDto {
    private final Long id;
    private final String intro;
    private final String name;
    private final Boolean isDelete;
}
