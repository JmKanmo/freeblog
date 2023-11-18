package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostImageResultDto {
    private final String imageSrc;
    private final String metaKey;
    private final String message;

    public static PostImageResultDto from(String imageSrc, String metaKey) {
        return PostImageResultDto.builder()
                .imageSrc(imageSrc)
                .metaKey(metaKey)
                .message("success")
                .build();
    }

    public static PostImageResultDto from(String imageSrc, String metaKey, String message) {
        return PostImageResultDto.builder()
                .imageSrc(imageSrc)
                .metaKey(metaKey)
                .message(message)
                .build();
    }
}
