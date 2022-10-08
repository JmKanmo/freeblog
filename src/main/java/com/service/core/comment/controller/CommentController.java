package com.service.core.comment.controller;

import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.service.CommentService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.CommentManageException;
import com.service.core.user.dto.UserCommentDto;
import com.service.core.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    @Operation(summary = "특정 포스트 댓글 반환", description = "특정 포스트 댓글 반환 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 포스트 댓글 반환 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 특정 포스트 댓글 반환 실패")
    })
    @GetMapping("/update/{commentId}")
    public String commentUpdatePage(@PathVariable Long commentId, Model model, Principal principal) {
        // TODO commentId를 토대로 anonymous 조사 및 principal을 통핸 로그인 여부 확인
        // 정보가 맞지 않을 경우, 예외 반환.
        CommentDto commentDto = commentService.findCommentDtoById(commentId);

        if (!commentDto.isAnonymous()) {
            if (principal == null) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_AUTHORITY_COMMENT);
            }

            UserCommentDto userCommentDto = userService.findUserCommentDtoByEmail(principal.getName());

            if (!userCommentDto.getUserId().equals(commentDto.getUserId()) || !BCrypt.checkpw(userCommentDto.getUserPassword(), commentDto.getUserPassword())) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_AUTHORITY_COMMENT);
            }
        }
        model.addAttribute("password_auth", commentDto.isAnonymous());
        return "comment/post-comment-update";
    }
}
