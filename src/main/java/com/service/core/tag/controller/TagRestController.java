package com.service.core.tag.controller;

import com.service.core.blog.service.BlogService;
import com.service.core.post.dto.PostPagingResponseDto;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.core.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagRestController {
    private final PostService postService;
    private final BlogService blogService;

    @Operation(summary = "태그 키워드 검색 결과 포스트 반환", description = "태그 키워드에 해당하는 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 포스트 데이터 반환 실패")
    })
    @GetMapping("/search-post")
    public ResponseEntity<PostPagingResponseDto> searchPostByTag(@RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId,
                                                                 @RequestParam(value = "tagKeyword", required = false, defaultValue = "") String tagKeyword,
                                                                 @ModelAttribute PostSearchPagingDto postSearchPagingDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(
                    PostPagingResponseDto.success(postService.findPostSearchPaginationByTagKeyword(BlogPostSearchInput.from(blogId, tagKeyword), postSearchPagingDto))
            );
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostPagingResponseDto.fail(exception));
        }
    }
}
