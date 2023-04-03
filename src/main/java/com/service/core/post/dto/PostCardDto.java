package com.service.core.post.dto;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

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
                .registerTime(BlogUtil.formatLocalDateTimeToStrByPattern(postDetailDto.getRegisterLocalDateTime(), "yyyy.MM.dd HH:mm"))
                .blogId(postDetailDto.getBlogId())
                .build();
    }
}
