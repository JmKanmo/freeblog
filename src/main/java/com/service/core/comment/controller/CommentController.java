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

    @Operation(summary = "답글 작성 페이지 반환", description = "답글 작성 페이지 반환 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글 작성 페이지 반환 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 답글 작성 페이지 반환 실패")
    })
    @GetMapping("/reply/{commentId}/{href}")
    public String commentReplyPage(@PathVariable Long commentId, @PathVariable String href, Model model) {
        CommentDto commentDto = commentService.findCommentDtoById(commentId);
        model.addAttribute("parent_comment_id", commentDto.getCommentId());
        model.addAttribute("target_user_id", commentDto.getUserId());
        model.addAttribute("comment_post_id", commentDto.getPostId());
        model.addAttribute("target_user_nickname", commentDto.getUserNickname());
        model.addAttribute("comment_href", href);
        return "comment/post-comment-reply";
    }

    @Operation(summary = "댓글 수정 페이지 반환", description = "댓글 수정 페이지 반환 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 페이지 반환 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 댓글 수정 페이지 반환 실패")
    })
    @GetMapping("/update/{commentId}/{href}")
    public String commentUpdatePage(@PathVariable Long commentId, @PathVariable String href, Model model, Principal principal) {
        CommentDto commentDto = commentService.findCommentDtoById(commentId);

        if (!commentDto.isAnonymous()) {
            if ((principal == null || principal.getName() == null)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_AUTHORITY_COMMENT);
            }

            UserCommentDto userCommentDto = userService.findUserCommentDtoByEmail(principal.getName());

            if (!userCommentDto.getUserId().equals(commentDto.getUserId()) || !BCrypt.checkpw(userCommentDto.getUserPassword(), commentDto.getUserPassword())) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_AUTHORITY_COMMENT);
            }
        }
        model.addAttribute("is_anonymous", commentDto.isAnonymous());
        model.addAttribute("comment_id", commentDto.getCommentId());
        model.addAttribute("comment_text", commentDto.getComment());
        model.addAttribute("comment_image", commentDto.getCommentImage());
        model.addAttribute("comment_nickname", commentDto.getUserNickname());
        model.addAttribute("comment_secret", commentDto.isSecret());
        model.addAttribute("comment_href", href);
        model.addAttribute("comment_metakey", commentDto.getMetaKey());
        return "comment/post-comment-update";
    }
}
