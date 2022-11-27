package com.service.core.comment.dto;

import com.service.core.comment.paging.CommentPaginationResponse;
import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class CommentPagingResponseDto {
    private final String message;
    private final int responseCode;
    private final CommentPaginationResponse<CommentSummaryDto> commentPaginationResponse;

    public static CommentPagingResponseDto success(CommentPaginationResponse<CommentSummaryDto> commentPaginationResponse) {
        return CommentPagingResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .commentPaginationResponse(commentPaginationResponse)
                .message("success")
                .build();
    }

    public static CommentPagingResponseDto fail(Exception exception) {
        return CommentPagingResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .commentPaginationResponse(null)
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .build();
    }
}
