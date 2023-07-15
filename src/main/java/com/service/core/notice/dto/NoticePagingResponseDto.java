package com.service.core.notice.dto;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class NoticePagingResponseDto<T> {
    private final String message;
    private final int responseCode;
    private final T noticePaginationResponse;

    public static <T> NoticePagingResponseDto success(T noticePaginationResponse) {
        return NoticePagingResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .noticePaginationResponse(noticePaginationResponse)
                .message("success")
                .build();
    }

    public static NoticePagingResponseDto fail(Exception exception) {
        return NoticePagingResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .noticePaginationResponse(null)
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .build();
    }
}
