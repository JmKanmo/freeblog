package com.service.core.post.dto;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class PostResponseDto<T> {
    private final String message;
    private final int responseCode;
    private final T postDto;

    public static <T> PostResponseDto success(T postDto) {
        return PostResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .postDto(postDto)
                .message("success")
                .build();
    }

    public static <T> PostResponseDto success(T postDto, String message) {
        return PostResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .postDto(postDto)
                .message(message)
                .build();
    }

    public static PostResponseDto fail(Exception exception) {
        return PostResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .postDto(null)
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .build();
    }

    public static PostResponseDto fail(String message) {
        return PostResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .postDto(null)
                .message(message)
                .build();
    }
}
