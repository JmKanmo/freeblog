package com.service.core.like.controller;

import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.dto.UserLikePostDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.service.LikeService;
import com.service.util.BlogUtil;
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

    @Operation(summary = "포스트 좋아요", description = "포스트 좋아요 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포스트 좋아요 누르기"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 포스트 좋아요 실패")
    })
    @PostMapping("/post")
    public ResponseEntity<String> postLike(@RequestBody LikePostInput likePostInput, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(likeService.postLike(principal, likePostInput));
        } catch (Exception exception) {
            log.error("[freeblog-postLike] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("작업에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

    @Operation(summary = "좋아요 목록 보기", description = "좋아요 목록 보기 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 목록 보기 반환"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 좋아요 목록 보기 실패")
    })
    @GetMapping("/user-post")
    public ResponseEntity<UserLikePostDto> userPostLike(Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(likeService.getUserLikePostDto(principal));
        } catch (Exception exception) {
            log.error("[freeblog-userPostLike] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserLikePostDto.fail(exception));
        }
    }
}
