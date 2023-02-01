package com.service.core.category.controller;

import com.service.core.category.dto.CategoryResponseDto;
import com.service.core.category.model.CategoryInput;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.CategoryManageException;
import com.service.core.post.dto.PostPagingResponseDto;
import com.service.core.post.paging.PostSearchPagingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "카테고리", description = "카테고리 관련 Rest API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryRestController {
    private final CategoryService categoryService;

    @Operation(summary = "해당 블로그의 전체 카테고리 반환", description = "해당 블로그의 전체 카테고리 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 카테고리 데이터 반환"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 반환 실패")
    })
    @GetMapping("/all/{blogId}")
    public ResponseEntity<CategoryResponseDto> findTotalCategoryByBlogId(@PathVariable Long blogId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(CategoryResponseDto.success(categoryService.findCategoryDtoByBlogId(blogId)));
        } catch (Exception exception) {
            log.error("[freeblog-findTotalCategoryByBlogId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CategoryResponseDto.fail(exception));
        }
    }

    @Operation(summary = "전체 카테고리의 모든 포스트 반환", description = "전체 카테고리의 모든 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 카테고리 모든 포스트 반환"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 반환 실패")
    })
    @GetMapping("/post/all/{blogId}")
    public ResponseEntity<PostPagingResponseDto> findTotalPostByBlogId(@PathVariable Long blogId, @ModelAttribute PostSearchPagingDto postSearchPagingDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostPagingResponseDto.success(categoryService.findPaginationPostByBlogId(blogId, postSearchPagingDto)));
        } catch (Exception exception) {
            log.error("[freeblog-findTotalPostByBlogId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "해당 카테고리의 전체 포스트 반환", description = "해당 카테고리의 전체 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 카테고리 전체 포스트 반환"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 반환 실패")
    })
    @GetMapping("/post/{categoryId}")
    public ResponseEntity<PostPagingResponseDto> findTotalPostByCategoryId(@PathVariable Long categoryId, @ModelAttribute PostSearchPagingDto postSearchPagingDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostPagingResponseDto.success(categoryService.findPaginationPostByCategoryId(categoryId, postSearchPagingDto)));
        } catch (Exception exception) {
            log.error("[freeblog-findTotalPostByCategoryId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "카테고리 등록", description = "카테고리 등록 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 등록 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 카테고리 등록 실패")
    })
    @PostMapping("/register/{blogId}")
    public ResponseEntity<CategoryResponseDto> registerCategory(@RequestBody @Valid List<CategoryInput> categoryInput, BindingResult bindingResult, @PathVariable Long blogId) {
        try {
            if (bindingResult.hasErrors()) {
                throw new CategoryManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
            categoryService.registerCategory(blogId, categoryInput);
            return ResponseEntity.status(HttpStatus.OK).body(CategoryResponseDto.success(null));
        } catch (Exception exception) {
            log.error("[freeblog-findTotalCategoryByBlogId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CategoryResponseDto.fail(exception));
        }
    }
}
