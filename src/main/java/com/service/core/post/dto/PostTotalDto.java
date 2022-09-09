package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostTotalDto {
    private final PostSummaryDto postSummaryDto;
    private final List<PostDto> postDtoList;

    public static PostTotalDto fromPostDtoList(List<PostDto> postDtoList, String type) {
        return PostTotalDto.builder()
                .postSummaryDto(PostSummaryDto.from(postDtoList, type))
                .postDtoList(postDtoList)
                .build();
    }

    public static PostTotalDto fromPostDtoList(List<PostDto> postDtoList, int count, String type) {
        return PostTotalDto.builder()
                .postSummaryDto(PostSummaryDto.from(count, type))
                .postDtoList(postDtoList)
                .build();
    }
}
