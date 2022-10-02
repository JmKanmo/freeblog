package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSearchDto {
    private final Long blogId;
    private final com.service.core.post.paging.PostSearchDto postSearchDto;

    public static PostSearchDto from(Long blogId, com.service.core.post.paging.PostSearchDto postSearchDto) {
        return com.service.core.post.dto.PostSearchDto.builder().blogId(blogId).postSearchDto(postSearchDto).build();
    }
}
