package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostTagKeywordDto {
    private final List<PostSearchMapperDto> postDtoList;

    public static PostTagKeywordDto from(List<PostSearchMapperDto> postDtoList) {
        return PostTagKeywordDto.builder()
                .postDtoList(postDtoList)
                .build();
    }
}
