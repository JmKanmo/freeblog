package com.service.core.post.dto;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class PostResponseDto {
    private final String message;
    private final int responseCode;
    private final PostTotalDto postTotalDto;

    public static PostResponseDto success(PostTotalDto postTotalDto) {
        return PostResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .postTotalDto(postTotalDto)
                .message("success")
                .build();
    }

    public static PostResponseDto fail(Exception exception) {
        return PostResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .postTotalDto(null)
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .build();
    }
}
