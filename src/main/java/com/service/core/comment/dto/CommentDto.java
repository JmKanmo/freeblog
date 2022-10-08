package com.service.core.comment.dto;

import com.service.core.comment.domain.Comment;
import com.service.util.BlogUtil;
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

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .commentId(comment.getId())
                .comment(comment.getComment())
                .commentImage(comment.getCommentImage())
                .parentId(comment.getParentId())
                .registerTime(BlogUtil.formatLocalDateTimeToStr(comment.getRegisterTime(), "yyyy.MM.dd HH:mm"))
                .userId(comment.getCommentUser().getUserId())
                .userPassword(comment.getCommentUser().getUserPassword())
                .userProfileImage(comment.getCommentUser().getUserProfileImage())
                .userNickname(comment.getCommentUser().getUserNickname())
                .targetUserId(comment.getCommentUser().getTargetUserId())
                .targetUserNickname(comment.getCommentUser().getTargetUserNickname())
                .isOwner(comment.getCommentUser().isOwner())
                .secret(comment.isSecret())
                .anonymous(comment.isAnonymous())
                .build();
    }
}
