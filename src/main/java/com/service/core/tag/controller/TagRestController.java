package com.service.core.tag.controller;

import com.service.core.blog.service.BlogService;
import com.service.core.post.dto.PostPagingResponseDto;
import com.service.core.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagRestController {
    private final PostService postService;
    private final BlogService blogService;

    // TODO

    @Operation(summary = "키워드 검색 결과 포스트 반환", description = "검색 키워드에 해당하는 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 포스트 데이터 반환 실패")
    })
    @GetMapping("/search-post")
    public ResponseEntity<PostPagingResponseDto> searchPostByTag() {
        // TODO
        return null;
    }
}
