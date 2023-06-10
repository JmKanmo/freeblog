package com.service.core.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogDeleteDto {
    private final long id;
    private final boolean isDelete;

    public static BlogDeleteDto from(BlogDeleteMapperDto blogDeleteMapperDto) {
        return BlogDeleteDto.builder()
                .id(blogDeleteMapperDto.getId())
                .isDelete(blogDeleteMapperDto.getIsDelete())
                .build();
    }
}
