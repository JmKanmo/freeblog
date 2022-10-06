package com.service.core.post.dto;

import com.service.core.post.paging.PostSearchPagingDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSearchDto {
    private final Long blogId;
    private final PostSearchPagingDto postSearchPagingDto;

    public static PostSearchDto from(Long blogId, PostSearchPagingDto postSearchPagingDto) {
        return PostSearchDto.builder().blogId(blogId).postSearchPagingDto(postSearchPagingDto).build();
    }
}
