package com.service.core.music.dto;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class MusicPagingResponseDto<T> {
    private final String message;
    private final int responseCode;
    private final T musicPaginationResponse;

    public static <T> MusicPagingResponseDto success(T musicPaginationResponse) {
        return MusicPagingResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .musicPaginationResponse(musicPaginationResponse)
                .message("success")
                .build();
    }

    public static MusicPagingResponseDto fail(Exception exception) {
        return MusicPagingResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .musicPaginationResponse(null)
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .build();
    }
}
