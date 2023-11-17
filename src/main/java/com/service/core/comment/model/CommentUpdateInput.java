package com.service.core.comment.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class CommentUpdateInput {
    @Size(max = 255, message = "댓글 이미지는 최대 255글자 까지 작성 가능합니다.")
    private final String commentThumbnailImage;

    private final String secretComment;

    private final String commentUserNickname;

    private final String commentUserPassword;

    private final String authCheckPassword;

    private final Long commentId;

    private final Boolean isAnonymous;

    private final String metaKey;

    @NotEmpty(message = "댓글 내용이 비어있습니다.")
    @NotBlank(message = "댓글 내용은 공백만 올 수 없습니다.")
    @Size(max = 2000, message = "댓글 내용은 최대 2000글자 까지 작성 가능합니다.")
    private final String comment;
}
