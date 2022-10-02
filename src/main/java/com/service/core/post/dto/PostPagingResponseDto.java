package com.service.core.post.dto;

import com.service.core.post.paging.PostPaginationResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class PostPagingResponseDto {
    private final String message;
    private final int responseCode;
    private final PostPaginationResponse<PostTotalDto> postPaginationResponse;

    public static PostPagingResponseDto success(PostPaginationResponse<PostTotalDto> postPaginationResponse) {
        return PostPagingResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .postPaginationResponse(postPaginationResponse)
                .message("success")
                .build();
    }

    public static PostPagingResponseDto fail(Exception exception) {
        return PostPagingResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .postPaginationResponse(null)
                .message(String.format("fail: %s", exception.getMessage()))
                .build();
    }
}
