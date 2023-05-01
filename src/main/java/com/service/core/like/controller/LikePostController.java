package com.service.core.like.controller;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.LikeManageException;
import com.service.core.like.dto.LikePagingResponseDto;
import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.dto.PostLikeResultDto;
import com.service.core.like.dto.UserLikePostDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.paging.LikeSearchPagingDto;
import com.service.core.like.service.LikeService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "좋아요", description = "좋아요 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
@Slf4j
public class LikePostController {
    private final LikeService likeService;

    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 누르기"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 게시글 좋아요 실패")
    })
    @PostMapping("/post")
    public ResponseEntity<PostLikeResultDto> postLike(@RequestBody LikePostInput likePostInput, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostLikeResultDto.success(likeService.postLike(principal, likePostInput)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-postLike] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostLikeResultDto.fail(exception));
        }
    }

    @Operation(summary = "사용자가 좋아요 누른 게시글 목록 조회", description = "사용자가 좋아요 누른 게시글 목록 조회 메서드 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자가 좋아요 누른 게시글 목록 반환"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 사용자가 좋아요 누른 게시글 목록 보기 실패")
    })
    @GetMapping("/post/user-list")
    public ResponseEntity<UserLikePostDto> postLikeUserList(Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(likeService.getUserLikePostDto(principal));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-postLikeUserList] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserLikePostDto.fail(exception));
        }
    }

    @Operation(summary = "게시글에 좋아요 누른 사용자 목록 조회", description = "게시글에 좋아요 누른 사용자 목록 조회 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글에 좋아요 누른 사용자 목록 조회"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 게시글에 좋아요 누른 사용자 목록 조회 실패")
    })
    @GetMapping("/post/liked/{postId}")
    public ResponseEntity<LikePagingResponseDto> findLikedPost(@PathVariable Long postId,
                                                               @RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId,
                                                               @ModelAttribute LikeSearchPagingDto likeSearchPagingDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(LikePagingResponseDto.success(likeService.getPostLikeDto(postId, blogId, likeSearchPagingDto)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-findLikedPost] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(LikePagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "게시글에 좋아요 누른 게시글 정보 삭제", description = "게시글에 좋아요 누른 게시글 정보 삭제 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자가 좋아요 누른 게시글 정보를 삭제"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 사용자가 좋아요 누른 게시글 정보 삭제 실패")
    })
    @DeleteMapping("/post/user-list")
    public ResponseEntity<String> deleteUserLikedPost(@RequestParam(value = "userId", required = false, defaultValue = ConstUtil.UNDEFINED) String userId,
                                                      @RequestParam(value = "postId", required = false, defaultValue = "0") Long postId,
                                                      Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                throw new LikeManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
            }

            likeService.deleteUserLikedPost(userId, postId);
            return ResponseEntity.status(HttpStatus.OK).body("게시글 정보를 삭제하였습니다.");
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-deleteUserLikedPost] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("좋아요 누른 게시글 삭제에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

    @Operation(summary = "게시글에 좋아요 누른 게시글 정보 전체 삭제", description = "게시글에 좋아요 누른 게시글 정보 전체 삭제 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자가 좋아요 누른 게시글 정보를 전체 삭제"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 사용자가 좋아요 누른 게시글 정보 전체 삭제 실패")
    })
    @DeleteMapping("/post/all-user-list")
    public ResponseEntity<String> deleteAllUserLikedPost(@RequestParam(value = "userId", required = false, defaultValue = ConstUtil.UNDEFINED) String userId,
                                                         Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                throw new LikeManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
            }

            likeService.deleteUserLikedPost(userId);
            return ResponseEntity.status(HttpStatus.OK).body("게시글 정보를 삭제하였습니다.");
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-deleteUserLikedPost] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("좋아요 누른 게시글 삭제에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }
}
