package com.service.core.video.dto;

import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoTokenDto {
    /**
     * video src 생성 등을 위한 응답 DTO
     * token,hash ...
     */
    private final String message;
    private final int responseCode;
    private final int hash;
    private final String token;
    private final String uploadType;

    public static VideoTokenDto success(String token) {
        return VideoTokenDto.builder()
                .token(token)
                .hash(ConstUtil.POST_VIDEO_HASH)
                .uploadType("videos")
                .message("success")
                .responseCode(200)
                .build();
    }

    public static VideoTokenDto fail(String message) {
        return VideoTokenDto.builder()
                .token(ConstUtil.UNDEFINED)
                .hash(ConstUtil.POST_VIDEO_HASH)
                .uploadType("videos")
                .message(message)
                .responseCode(500)
                .build();
    }
}
