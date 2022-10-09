package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.security.Principal;

@Data
@Builder
public class CommentDelAuthDto {
    private final String message;
    private final boolean auth;
    private final boolean login;

    public static CommentDelAuthDto success(boolean auth, Principal principal) {
        return CommentDelAuthDto.builder()
                .message("권한이 확인되었습니다.")
                .auth(auth)
                .login((principal == null || principal.getName() == null) ? false : true)
                .build();
    }

    public static CommentDelAuthDto fail(Exception exception) {
        return CommentDelAuthDto.builder()
                .message(String.format("댓글 삭제 권한 확인에 실패하였습니다. %s", exception.getMessage()))
                .auth(false)
                .login(false)
                .build();
    }
}
