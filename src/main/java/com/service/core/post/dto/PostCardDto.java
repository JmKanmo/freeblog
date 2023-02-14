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
}
