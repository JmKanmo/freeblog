package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentPagingResponseDto {
    private final String message;
    private final int responseCode;

}
