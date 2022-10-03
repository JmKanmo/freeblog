package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentChildDto {
    private final Long id;
    private final Long parentId;
    private final String comment;
    private final String commentImage;
    private final String userId;
    private final String userProfileImage;
    private final String userNickname;
    private final String targetUserId;
    private final String targetUserNickname;
    private final boolean isOwner;
    private final boolean secret;
    private final String registerTime;
}
