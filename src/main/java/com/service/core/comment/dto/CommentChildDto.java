package com.service.core.comment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private final String href;
    @JsonIgnore
    private final String userPassword;
    private final String userProfileImage;
    private final String userNickname;
    private final String targetUserId;
    private final String targetUserNickname;
    private final String metaKey;
    private final boolean isOwner;
    private final boolean isDelete;
    private boolean secret;
    private final boolean anonymous;
    private final String registerTime;
    private final Boolean isBaseTimezone;

    public static CommentChildDto from(CommentDto commentDto) {
        return CommentChildDto.builder()
                .id(commentDto.getCommentId())
                .parentId(commentDto.getParentId())
                .href(commentDto.getHref())
                .comment(commentDto.getComment())
                .commentImage(commentDto.getCommentImage())
                .userId(commentDto.getUserId())
                .userPassword(commentDto.getUserPassword())
                .userProfileImage(commentDto.getUserProfileImage())
                .userNickname(commentDto.getUserNickname())
                .targetUserId(commentDto.getTargetUserId())
                .targetUserNickname(commentDto.getTargetUserNickname())
                .metaKey(commentDto.getMetaKey())
                .isOwner(commentDto.isOwner())
                .isDelete(commentDto.isDelete())
                .secret(commentDto.isSecret())
                .anonymous(commentDto.isAnonymous())
                .registerTime(commentDto.getRegisterTime())
                .isBaseTimezone(commentDto.getIsBaseTimezone())
                .build();
    }
}
