package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostSummaryDto {
    private final String type;
    private final Integer count;

    public static PostSummaryDto from(List<PostDto> postDtoList, String type) {
        return PostSummaryDto.builder()
                .type(type)
                .count(postDtoList.size())
                .build();
    }
}
