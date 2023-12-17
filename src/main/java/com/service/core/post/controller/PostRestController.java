package com.service.core.post.controller;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogDeleteDto;
import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.category.domain.Category;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogManageException;
import com.service.core.error.model.PostManageException;
import com.service.core.error.model.UserManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.*;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.core.post.service.PostPopularService;
import com.service.core.post.service.PostService;
import com.service.core.user.dto.UserHeaderDto;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    private final PostPopularService postPopularService;
    private final BlogService blogService;

    private final UserService userService;

    private final CategoryService categoryService;

    @Operation(summary = "블로그 포스트 작성 작업", description = "블로그 포스트 작성 페이지 작업 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 작성 작업 성공"),
            @ApiResponse(responseCode = "500", description = "DB 연결 오류, SQL 쿼리 수행 실패 등의 이유로 블로그 포스트 작성 작업 실패")
    })
    @PostMapping("/write/{userId}")
    public ResponseEntity<PostResponseDto<PostDetailDto>> postWrite(@Valid BlogPostInput blogPostInput, BindingResult bindingResult, Model model, Principal principal) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }

            if (bindingResult.hasErrors()) {
                throw new PostManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }

            Post post = Post.from(blogPostInput);
            UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
            BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoByEmail(principal.getName());

            if (blogDeleteDto.getId() != blogPostInput.getId()) {
                throw new BlogManageException(ServiceExceptionMessage.MISMATCH_BLOG_INFO);
            }
            post.setWriter(userHeaderDto.getNickname());
            post.setBlog(Blog.builder().id(blogDeleteDto.getId()).build());
            post.setCategory(
                    Category.builder()
                            .id(categoryService.findCategoryBasicMapperDtoByCategoryIdAndEmail(blogPostInput.getCategory(), principal.getName()).getId())
                            .build());
            post.setSeq((long) (postService.findPostCountByBlogId(blogDeleteDto.getId()) + 1));
            post.setMetaKey(blogPostInput.getMetaKey());
            Post writedPost = postService.register(post, blogPostInput);
            PostDetailDto postDetailDto = postService.updateCachePostDetailInfo(blogPostInput.getId(), writedPost.getId(), writedPost);
            return ResponseEntity.status(HttpStatus.OK).body(PostResponseDto.success(postDetailDto, "게시글 작성이 완료되었습니다. 작성 된 게시글을 확인하려면 페이지를 새로고침 해주세요."));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-postWrite] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostResponseDto.fail(String.format("게시글 업로드에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception))));
        }
    }

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
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-searchPostByKeyword] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostResponseDto.fail(exception));
        }
    }

    @Operation(summary = "인기 포스트 반환", description = "인기 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인기 포스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 인기 포스트 데이터 반환 실패")
    })
    @GetMapping("/popular/{blogId}")
    public ResponseEntity<PostResponseDto<List<PostCardDto>>> searchPopularPost(@PathVariable Long blogId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostResponseDto.success(postPopularService.findPopularPost(blogId)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-searchPopularPost] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostResponseDto.fail(exception));
        }
    }

    @Operation(summary = "키워드 검색 결과 포스트 반환", description = "검색 키워드에 해당하는 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 포스트 데이터 반환 실패")
    })
    @GetMapping("/search-rest")
    public ResponseEntity<PostPagingResponseDto> searchPostByKeyword(@ModelAttribute @Valid BlogPostSearchInput blogPostSearchInput,
                                                                     @ModelAttribute PostSearchPagingDto postSearchPagingDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostPagingResponseDto.success(postService.findPostSearchPaginationByKeyword(blogPostSearchInput, postSearchPagingDto)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-searchPostByKeyword] exception occurred ", exception);
            }
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
            return ResponseEntity.status(HttpStatus.OK).body(PostPagingResponseDto.success(postService.findTotalPaginationPost(blogService.findBlogDeleteDtoByBlogId(blogId).getId(), postSearchPagingDto, ConstUtil.TOTAL_POST)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-findTotalPostByBlogId] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "게시글 이미지 업로드", description = "게시글 이미지 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 이미지 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 게시글 이미지 업로드 실패")
    })
    @PostMapping("/upload/post-image/{uploadKey}")
    public ResponseEntity<String> uploadPostImage(@RequestParam("compressed_post_image") MultipartFile multipartFile, Principal principal, @PathVariable String uploadKey) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            return ResponseEntity.status(HttpStatus.OK).body(postService.uploadSftpPostImage(multipartFile, uploadKey));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-uploadPostThumbnailImage] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("게시글 이미지 업로드에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

//    @Operation(summary = "게시글 이미지 삭제", description = "게시글 이미지 삭제 수행 메서드")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시글 이미지 삭제 완료"),
//            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 게시글 이미지 삭제 실패")
//    })
//    @PostMapping("/delete/post-image")
//    public ResponseEntity<String> deletePostImage(@RequestParam("imgSrcList") List<String> imgSrcList, Principal principal) {
//        try {
//            if ((principal == null || principal.getName() == null)) {
//                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
//            }
//            postService.deleteSftpPostImage(imgSrcList);
//            return ResponseEntity.status(HttpStatus.OK).body("게시글 이미지를 삭제하였습니다.");
//        } catch (Exception exception) {
//            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
//                log.error("[freeblog-deletePostImage] exception occurred ", exception);
//            }
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("게시글 이미지 삭제에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
//        }
//    }

    @Operation(summary = "포스트 썸네일 이미지 업로드", description = "포스트 썸네일 이미지 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 포스트 썸네일 이미지 업로드 실패")
    })
    @PostMapping("/upload/post-thumbnail-image")
    public ResponseEntity<PostImageResultDto> uploadPostThumbnailImage(@RequestParam("compressed_post_image") MultipartFile multipartFile,
                                                                       @RequestParam(value = "uploadType", required = false, defaultValue = ConstUtil.UPLOAD_TYPE_S3) String uploadType,
                                                                       @RequestParam(value = "uploadKey", required = false, defaultValue = ConstUtil.UNDEFINED) String uploadKey,
                                                                       Principal principal) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            PostImageResultDto postImageResultDto = null;

            if (uploadType.equals(ConstUtil.UPLOAD_TYPE_S3)) {
                postImageResultDto = PostImageResultDto.from(postService.uploadAwsS3PostThumbnailImage(multipartFile), null);
            } else if (uploadType.equals(ConstUtil.UPLOAD_TYPE_FILE_SERVER)) {
                postImageResultDto = PostImageResultDto.from(postService.uploadSftpPostThumbnailImage(multipartFile, uploadKey), uploadKey);
            }
            return ResponseEntity.status(HttpStatus.OK).body(postImageResultDto);
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-uploadPostThumbnailImage] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostImageResultDto.from(null, null, String.format("포스트 썸네일 이미지 업로드에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception))));
        }
    }

//    @Operation(summary = "게시글 썸네일 이미지 삭제", description = "게시글 썸네일 이미지 삭제 수행 메서드")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "게시글 썸네일 이미지 삭제 완료"),
//            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 게시글 썸네일 이미지 삭제 실패")
//    })
//    @PostMapping("/delete/post-thumbnail-image")
//    public ResponseEntity<String> deletePostThumbnailImage(@RequestParam(value = "imgSrc", defaultValue = ConstUtil.UNDEFINED) String imgSrc, Principal principal) {
//        try {
//            if ((principal == null || principal.getName() == null)) {
//                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
//            }
//
//            if (imgSrc != null && !imgSrc.isEmpty() && !imgSrc.equals(ConstUtil.UNDEFINED)) {
//                postService.deleteSftpPostImage(imgSrc);
//            }
//            return ResponseEntity.status(HttpStatus.OK).body("게시글 썸네일 이미지를 삭제하였습니다.");
//        } catch (Exception exception) {
//            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
//                log.error("[freeblog-deletePostImage] exception occurred ", exception);
//            }
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("게시글 이미지 삭제에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
//        }
//    }
}
