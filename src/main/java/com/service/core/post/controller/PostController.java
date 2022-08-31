package com.service.core.post.controller;

import com.service.core.post.dto.PostResponseDto;
import com.service.core.post.service.PostService;
import com.service.util.ConstUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포스트", description = "포스트 관련 API")
@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
@Slf4j
public class PostController {
    private final PostService postService;

    @Operation(summary = "해당 블로그의 전체 포스트 반환", description = "해당 블로그의 전체 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 전체 포스트 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 반환 실패")
    })
    @ResponseBody
    @GetMapping("/all/{blogId}")
    public ResponseEntity<PostResponseDto> findTotalPostByBlogId(@PathVariable Long blogId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostResponseDto.success(postService.findTotalPost(blogId, ConstUtil.TOTAL_POST)));
        } catch (Exception exception) {
            log.error("[freelog-findTotalPostByBlogId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostResponseDto.fail(exception));
        }
    }
}
