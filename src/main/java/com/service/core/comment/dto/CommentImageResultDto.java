package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentImageResultDto {
    private final String imageSrc;
    private final String metaKey;
    private final String message;

    public static CommentImageResultDto from(String imageSrc, String metaKey) {
        return CommentImageResultDto.builder()
                .imageSrc(imageSrc)
                .metaKey(metaKey)
                .message("success")
                .build();
    }

    public static CommentImageResultDto from(String imageSrc, String metaKey, String message) {
        return CommentImageResultDto.builder()
                .imageSrc(imageSrc)
                .metaKey(metaKey)
                .message(message)
                .build();
    }
}
