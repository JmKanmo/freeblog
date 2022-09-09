package com.service.core.post.dto;

import com.service.util.paging.PaginationResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class PostPagingResponseDto {
    private final String message;
    private final int responseCode;
    private final PaginationResponse<PostTotalDto> paginationResponse;

    public static PostPagingResponseDto success(PaginationResponse<PostTotalDto> paginationResponse) {
        return PostPagingResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .paginationResponse(paginationResponse)
                .message("success")
                .build();
    }

    public static PostPagingResponseDto fail(Exception exception) {
        return PostPagingResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .paginationResponse(null)
                .message(String.format("fail: %s", exception.getMessage()))
                .build();
    }
}
