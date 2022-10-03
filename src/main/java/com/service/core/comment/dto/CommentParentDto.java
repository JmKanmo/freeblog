package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentParentDto {
    private final Long id;
    private final String comment;
    private final String commentImage;
    private final String userId;
    private final String userNickname;
    private final String userProfileImage;
    private final boolean isOwner;
    private final boolean secret;
    private final String registerTime;
}
