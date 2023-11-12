package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class CommentRegisterDto {
    private final String message;
    private final Integer commentCount;
    private Long commentId; // 추가 된 댓글 ID
    private final int responseCode;

    public static CommentRegisterDto success(Integer commentCount, Long commentId, String message) {
        return CommentRegisterDto.builder()
                .message(message)
                .commentCount(commentCount)
                .commentId(commentId)
                .responseCode(HttpStatus.OK.value())
                .build();
    }

    public static CommentRegisterDto fail(Exception exception) {
        return CommentRegisterDto.builder()
                .message(String.format("댓글 작성에 실패하였습니다. %s", exception.getMessage()))
                .commentCount(0)
                .commentId(0L)
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }
}
