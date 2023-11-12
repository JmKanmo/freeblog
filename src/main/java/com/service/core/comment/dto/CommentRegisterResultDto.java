package com.service.core.comment.dto;

import com.service.core.comment.domain.Comment;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRegisterResultDto {
    private final Integer commentCount;
    private Long commentId; // 추가 된 댓글 ID

    public static CommentRegisterResultDto from(Integer commentCount, Long commentId) {
        return CommentRegisterResultDto.builder()
                .commentCount(commentCount)
                .commentId(commentId)
                .build();
    }
}
