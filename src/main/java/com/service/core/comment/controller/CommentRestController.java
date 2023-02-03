package com.service.core.comment.controller;

import com.service.core.comment.dto.CommentDelAuthDto;
import com.service.core.comment.dto.CommentPagingResponseDto;
import com.service.core.comment.dto.CommentRegisterDto;
import com.service.core.comment.model.CommentInput;
import com.service.core.comment.model.CommentUpdateInput;
import com.service.core.comment.paging.CommentSearchPagingDto;
import com.service.core.comment.service.CommentService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.CommentManageException;
import com.service.util.BlogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentRestController {
    private final CommentService commentService;

    @Operation(summary = "특정 포스트 댓글 반환", description = "특정 포스트 댓글 반환 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 포스트 댓글 반환 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 특정 포스트 댓글 반환 실패")
    })
    @GetMapping("/{postId}/{blogId}")
    public ResponseEntity<CommentPagingResponseDto> findTotalCommentsByPostId(@PathVariable Long postId, @PathVariable Long blogId, @ModelAttribute CommentSearchPagingDto commentSearchPagingDto, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(CommentPagingResponseDto.success(commentService.findTotalPaginationComment(postId, blogId, commentSearchPagingDto, principal)));
        } catch (Exception exception) {
            log.error("[freeblog-findTotalCommentsByPostId] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommentPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 댓글 수정 실패")
    })
    @PutMapping("/update")
    public ResponseEntity<String> updateComment(@Valid CommentUpdateInput commentUpdateInput, BindingResult bindingResult, Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            } else if (!commentUpdateInput.getIsAnonymous() && (principal == null || principal.getName() == null)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_AUTHORITY_COMMENT);
            }
            commentService.updateComment(commentUpdateInput, principal);
            return ResponseEntity.status(HttpStatus.OK).body("댓글이 정상적으로 수정되었습니다. 페이지를 새로고침 후 확인해주세요.");
        } catch (Exception exception) {
            log.error("[freeblog-updateComment] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("댓글 수정 작업에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

    @Operation(summary = "댓글 업로드", description = "댓글 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 댓글 업로드 실패")
    })
    @PostMapping("/register")
    public ResponseEntity<CommentRegisterDto> registerComment(@Valid CommentInput commentInput, BindingResult bindingResult, Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
            return ResponseEntity.status(HttpStatus.OK).body(CommentRegisterDto.success(commentService.registerComment(commentInput, principal), "댓글 작성에 성공하였습니다."));
        } catch (Exception exception) {
            log.error("[freeblog-registerComment] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommentRegisterDto.fail(exception));
        }
    }

    @Operation(summary = "답글 업로드", description = "답글 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 답글 업로드 실패")
    })
    @PostMapping("/reply")
    public ResponseEntity<String> replyComment(@Valid CommentInput commentInput, BindingResult bindingResult, Principal principal) {
        try {
            if (bindingResult.hasErrors()) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
            commentService.registerReplyComment(commentInput, principal);
            return ResponseEntity.status(HttpStatus.OK).body("답글이 작성되었습니다. 페이지를 새로고침 해주세요.");
        } catch (Exception exception) {
            log.error("[freeblog-replyComment] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("답글 작성에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

    @Operation(summary = "댓글 썸네일 이미지 업로드", description = "댓글 썸네일 이미지 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 썸네일 이미지 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 댓글 썸네일 이미지 업로드 실패")
    })
    @PostMapping("/upload/comment-thumbnail-image")
    public ResponseEntity<String> uploadCommentThumbnailImage(@RequestParam("compressed_post_comment_image") MultipartFile multipartFile) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(commentService.uploadAwsSCommentThumbnailImage(multipartFile));
        } catch (Exception exception) {
            log.error("[freeblog-uploadCommentThumbnailImage] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("댓글 썸네일 이미지 업로드에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

    @Operation(summary = "댓글 삭제 권한 확인", description = "댓글 삭제 권한 확인 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 권한 확인 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 댓글 삭제 권한 확인 실패")
    })
    @GetMapping("/authority/{commentId}")
    public ResponseEntity<CommentDelAuthDto> checkAuthority(@PathVariable Long commentId, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(CommentDelAuthDto.success(commentService.checkAuthority(commentId, principal), principal));
        } catch (Exception exception) {
            log.error("[freeblog-checkAuthority] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommentDelAuthDto.fail(exception));
        }
    }

    @Operation(summary = "댓글 삭제", description = "댓글 삭제 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 댓글 삭제 실패")
    })
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, Principal principal, @RequestParam(value = "password", required = false, defaultValue = "") String password) {
        try {
            commentService.deleteComment(commentId, password, principal);
            return ResponseEntity.status(HttpStatus.OK).body("댓글이 삭제되었습니다.");
        } catch (Exception exception) {
            log.error("[freeblog-deleteComment] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("댓글 삭제에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }
}
