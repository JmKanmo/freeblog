package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostKeywordDto {
    private final List<PostSearchMapperDto> postDtoList;

    public static PostKeywordDto from(List<PostSearchMapperDto> postDtoList) {
        return PostKeywordDto.builder().postDtoList(postDtoList).build();
    }
}
