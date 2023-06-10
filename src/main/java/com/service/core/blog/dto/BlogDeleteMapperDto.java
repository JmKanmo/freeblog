package com.service.core.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogDeleteMapperDto {
    private final Long id;
    private final Boolean isDelete;
}
