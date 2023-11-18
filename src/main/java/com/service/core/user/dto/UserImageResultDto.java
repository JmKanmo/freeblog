package com.service.core.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserImageResultDto {
    private final String imageSrc;
    private final String metaKey;
    private final String message;

    public static UserImageResultDto from(String imageSrc, String metaKey) {
        return UserImageResultDto.builder()
                .imageSrc(imageSrc)
                .metaKey(metaKey)
                .message("success")
                .build();
    }

    public static UserImageResultDto from(String imageSrc, String metaKey, String message) {
        return UserImageResultDto.builder()
                .imageSrc(imageSrc)
                .metaKey(metaKey)
                .message(message)
                .build();
    }
}
