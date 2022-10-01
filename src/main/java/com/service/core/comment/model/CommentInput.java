package com.service.core.comment.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentInput {
    private final String commentThumbnailImage;
    private final boolean secretComment;
    private final String commentUserNickname;
    private final String commentUserPassword;
    private final String commentUserId;
    private final boolean commentIsAnonymous;
    private final Long commentPostId;
}
