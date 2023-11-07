package com.service.core.main.controller;

import com.service.core.main.model.MainPostSearchInput;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.core.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "메인화면", description = "메인화면 관련 REST API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MainRestController {
    private final PostService postService;

    @Operation(summary = "메인 게시글 검색", description = "메인 게시글 검색 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메인 게시글 검색 데이터 반환"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 메인 게시글 검색 데이터 반환 실패")
    })
    @GetMapping("/search-post")
    public String searchPost(@Valid MainPostSearchInput mainPostSearchInput, @ModelAttribute PostSearchPagingDto postSearchPagingDto) {
        // TODO
        return "index";
    }
}
