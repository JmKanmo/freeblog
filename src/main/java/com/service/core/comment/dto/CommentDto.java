package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {
    private final Long commentId;
    private final String comment;
    private final String commentImage;
    private final Long parentId;
    private final String registerTime;
    private final String userId;
    private final String userPassword;
    private final String userProfileImage;
    private final String userNickname;
    private final String targetUserId;
    private final String targetUserNickname;
    private final boolean isOwner;
    private final boolean secret;
    private final boolean anonymous;
}
