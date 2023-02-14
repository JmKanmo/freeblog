package com.service.core.comment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentParentDto {
    private final Long id;
    private final String comment;
    private final String commentImage;
    private final String userId;
    private final String href;
    @JsonIgnore
    private final String userPassword;
    private final String userNickname;
    private final String userProfileImage;
    private final boolean isOwner;
    private final boolean isDelete;
    private boolean secret;
    private final boolean anonymous;
    private final String registerTime;

    public static CommentParentDto from(CommentDto commentDto) {
        return CommentParentDto.builder()
                .id(commentDto.getCommentId())
                .comment(commentDto.getComment())
                .commentImage(commentDto.getCommentImage())
                .href(commentDto.getHref())
                .userId(commentDto.getUserId())
                .userPassword(commentDto.getUserPassword())
                .userNickname(commentDto.getUserNickname())
                .userProfileImage(commentDto.getUserProfileImage())
                .isOwner(commentDto.isOwner())
                .isDelete(commentDto.isDelete())
                .secret(commentDto.isSecret())
                .anonymous(commentDto.isAnonymous())
                .registerTime(commentDto.getRegisterTime())
                .build();
    }
}
