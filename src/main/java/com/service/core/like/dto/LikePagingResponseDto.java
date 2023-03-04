package com.service.core.like.dto;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class LikePagingResponseDto<T> {
    private final String message;
    private final int responseCode;
    private final T likePaginationResponse;

    public static <T> LikePagingResponseDto success(T likePaginationResponse) {
        return LikePagingResponseDto.builder()
                .message("success")
                .responseCode(HttpStatus.OK.value())
                .likePaginationResponse(likePaginationResponse)
                .build();
    }

    public static LikePagingResponseDto fail(Exception exception) {
        return LikePagingResponseDto.builder()
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .likePaginationResponse(null)
                .build();
    }
}
