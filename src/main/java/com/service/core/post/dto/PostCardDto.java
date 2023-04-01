package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCardDto {
    private final Long id;
    private final String title;
    private final String thumbnailImage;
    private final String registerTime;
    private final Long blogId;

    public static PostCardDto from(PostDetailDto postDetailDto) {
        return PostCardDto.builder()
                .id(postDetailDto.getId())
                .title(postDetailDto.getTitle())
                .thumbnailImage(postDetailDto.getThumbnailImage())
                .registerTime(postDetailDto.getRegisterTime())
                .blogId(postDetailDto.getBlogId())
                .build();
    }
}
