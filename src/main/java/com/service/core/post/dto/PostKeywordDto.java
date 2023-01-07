package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostKeywordDto {
    private final List<PostDto> postDtoList;

    public static PostKeywordDto from(List<PostDto> postDtoList) {
        return PostKeywordDto.builder().postDtoList(postDtoList).build();
    }
}
