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
    private boolean secret;
    private final boolean anonymous;
    private final String registerTime;

    public static CommentChildDto from(CommentDto commentDto) {
        return CommentChildDto.builder()
                .id(commentDto.getCommentId())
                .parentId(commentDto.getParentId())
                .comment(commentDto.getComment())
                .commentImage(commentDto.getCommentImage())
                .userId(commentDto.getUserId())
                .userProfileImage(commentDto.getUserProfileImage())
                .userNickname(commentDto.getUserNickname())
                .targetUserId(commentDto.getTargetUserId())
                .targetUserNickname(commentDto.getTargetUserNickname())
                .isOwner(commentDto.isOwner())
                .secret(commentDto.isSecret())
                .anonymous(commentDto.isAnonymous())
                .registerTime(commentDto.getRegisterTime())
                .build();
    }
}
