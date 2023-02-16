package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentLinkDto {
    private final Long postId;
    private final String comment;
    private final String href;
}
