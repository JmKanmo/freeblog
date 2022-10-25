package com.service.core.post.dto;

import com.service.core.post.paging.PostSearchPagingDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSearchDto {
    private final Long blogId;
    private final Long categoryId;
    private final PostSearchPagingDto postSearchPagingDto;

    public static PostSearchDto from(Long blogId, PostSearchPagingDto postSearchPagingDto) {
        return PostSearchDto.builder().blogId(blogId).postSearchPagingDto(postSearchPagingDto).build();
    }

    public static PostSearchDto from(Long blogId, Long categoryId, PostSearchPagingDto postSearchPagingDto) {
        return PostSearchDto.builder().blogId(blogId).categoryId(categoryId).postSearchPagingDto(postSearchPagingDto).build();
    }
}
