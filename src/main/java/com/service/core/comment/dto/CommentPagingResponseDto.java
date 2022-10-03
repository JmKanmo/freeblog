package com.service.core.comment.dto;

import com.service.core.comment.paging.CommentPaginationResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentPagingResponseDto {
    private final String message;
    private final int responseCode;
    private final CommentPaginationResponse<CommentTotalDto> commentPaginationResponse;

    // TODO
}
