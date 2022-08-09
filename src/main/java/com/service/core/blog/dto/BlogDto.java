package com.service.core.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogDto {
    private final long id;
    private final String name;
    private final String intro;
    // TODO
}
