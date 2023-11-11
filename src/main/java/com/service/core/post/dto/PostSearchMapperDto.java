package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSearchMapperDto {
    private final Long id;
    private final String title;
    private final String thumbnailImage;
    private final String summary;
    private final String writer;
    private final String registerTime;
    private final String category;
    private final Long categoryId;
    private final Long blogId;
    private final Long commentCount;
    private final Boolean isBaseTimezone;
}
