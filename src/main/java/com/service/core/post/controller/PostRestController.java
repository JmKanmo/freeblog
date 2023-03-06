package com.service.core.post.controller;

import com.service.core.blog.service.BlogService;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserManageException;
import com.service.core.post.dto.PostCardDto;
import com.service.core.post.dto.PostPagingResponseDto;
import com.service.core.post.dto.PostResponseDto;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.core.post.service.PostService;
import com.service.core.user.service.UserService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Tag(name = "포스트", description = "포스트 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
@Slf4j
public class PostRestController {
    private final PostService postService;
    private final BlogService blogService;

    @Operation(summary = "최신 포스트 반환", description = "최신 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "최신 포스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 최신 포스트 데이터 반환 실패")
    })
    @GetMapping("/recent/{blogId}")
    public ResponseEntity<PostResponseDto<List<PostCardDto>>> searchRecentPost(@PathVariable Long blogId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostResponseDto.success(postService.findRecentPostCardDtoByBlogId(blogId)));
        } catch (Exception exception) {
            log.error("[freeblog-searchPostByKeyword] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostResponseDto.fail(exception));
        }
    }

    @Operation(summary = "키워드 검색 결과 포스트 반환", description = "검색 키워드에 해당하는 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 포스트 데이터 반환 실패")
    })
    @GetMapping("/search-rest")
    public ResponseEntity<PostPagingResponseDto> searchPostByKeyword(@RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId,
                                                                     @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                                                     @ModelAttribute PostSearchPagingDto postSearchPagingDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostPagingResponseDto.success(postService.findPostSearchPaginationByKeyword(BlogPostSearchInput.from(blogId, keyword), postSearchPagingDto)));
        } catch (Exception exception) {
            log.error("[freeblog-searchPostByKeyword] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "해당 블로그의 전체 포스트 반환", description = "해당 블로그의 전체 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 전체 포스트 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 반환 실패")
    })
    @GetMapping("/all/{blogId}")
    public ResponseEntity<PostPagingResponseDto> findTotalPostByBlogId(@PathVariable Long blogId, @ModelAttribute PostSearchPagingDto postSearchPagingDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostPagingResponseDto.success(postService.findTotalPaginationPost(blogService.findBlogByIdOrThrow(blogId).getId(), postSearchPagingDto, ConstUtil.TOTAL_POST)));
        } catch (Exception exception) {
            log.error("[freeblog-findTotalPostByBlogId] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "게시글 이미지 업로드", description = "게시글 이미지 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 이미지 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 게시글 이미지 업로드 실패")
    })
    @PostMapping("/upload/post-image")
    public ResponseEntity<String> uploadPostImage(@RequestParam("compressed_post_image") MultipartFile multipartFile, Principal principal) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            return ResponseEntity.status(HttpStatus.OK).body(postService.uploadSftpPostImage(multipartFile));
        } catch (Exception exception) {
            log.error("[freeblog-uploadPostThumbnailImage] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("포스트 썸네일 이미지 업로드에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

    @Operation(summary = "포스트 썸네일 이미지 업로드", description = "포스트 썸네일 이미지 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 포스트 썸네일 이미지 업로드 실패")
    })
    @PostMapping("/upload/post-thumbnail-image")
    public ResponseEntity<String> uploadPostThumbnailImage(@RequestParam("compressed_post_image") MultipartFile multipartFile, Principal principal) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            return ResponseEntity.status(HttpStatus.OK).body(postService.uploadAwsS3PostThumbnailImage(multipartFile));
        } catch (Exception exception) {
            log.error("[freeblog-uploadPostThumbnailImage] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("포스트 썸네일 이미지 업로드에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }
}
