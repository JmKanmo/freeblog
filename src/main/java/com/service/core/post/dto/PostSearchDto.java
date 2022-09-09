package com.service.core.post.dto;

import com.service.util.paging.SearchDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSearchDto {
    private final Long blogId;
    private final SearchDto searchDto;

    public static PostSearchDto from(Long blogId, SearchDto searchDto) {
        return PostSearchDto.builder().blogId(blogId).searchDto(searchDto).build();
    }
}
