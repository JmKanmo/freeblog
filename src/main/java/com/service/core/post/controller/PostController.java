package com.service.core.post.controller;

import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostPagingResponseDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.paging.PostSearchDto;
import com.service.core.post.service.PostService;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.service.UserService;
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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;

@Tag(name = "포스트", description = "포스트 관련 API")
@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
@Slf4j
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final BlogService blogService;

    @Operation(summary = "포스트 상세 페이지 반환", description = "포스트 상세 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포스트 상세 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "포스트 상세 페이지 반환 실패")
    })
    @GetMapping("/{postId}")
    public String postDetailPage(@PathVariable Long postId, @RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId,
                                 Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("user_header", userService.findUserHeaderDtoByEmail(principal.getName()));
        }

        model.addAttribute("user_profile", userService.findUserProfileDtoByBlogId(blogId));
        model.addAttribute("postDetail", postService.findPostDetailInfo(blogId, postId));
        return "post/post-detail";
    }

    @Operation(summary = "블로그 포스트 작성 페이지 반환", description = "블로그 포스트 작성 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 작성 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "블로그 포스트 작성 페이지 반환 실패")
    })
    @GetMapping("/write/{userId}")
    public String postWritePage(@PathVariable String userId, Model model, Principal principal) {
        if (principal == null) {
            throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
        }

        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());

        if (!userId.equals(userHeaderDto.getId())) {
            throw new UserAuthException(ServiceExceptionMessage.MISMATCH_ID);
        }

        BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoById(userHeaderDto.getId());

        model.addAttribute("user_header", userHeaderDto);
        model.addAttribute("blogPostInput", BlogPostInput.builder().id(blogInfoDto.getId()).build());
        return "post/post-write";
    }

    @Operation(summary = "블로그 포스트 작성 작업", description = "블로그 포스트 작성 페이지 작업 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 작성 작업 성공"),
            @ApiResponse(responseCode = "500", description = "DB 연결 오류, SQL 쿼리 수행 실패 등의 이유로 블로그 포스트 작성 작업 실패")
    })
    @PostMapping("/write/{userId}")
    public String postWrite(@Valid BlogPostInput blogPostInput, BindingResult bindingResult, Model model, Principal principal) {
        if (principal == null) {
            throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
        }

        if (bindingResult.hasErrors()) {
            return "post/post-write";
        }

        Post post = Post.from(blogPostInput);
        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
        post.setWriter(userHeaderDto.getNickname());
        post.setBlog(blogService.findBlogByEmail(principal.getName()));
        post.setCategory(categoryService.findCategoryById(principal.getName(), blogPostInput.getCategory()));
        postService.register(post, blogPostInput);
        model.addAttribute("result", "게시글 작성이 완료되었습니다. 작성 된 게시글을 확인하려면 페이지를 새로고침 해주세요.");

        return String.format("redirect:/blog/%s", userHeaderDto.getId());
    }

    @Operation(summary = "해당 블로그의 전체 포스트 반환", description = "해당 블로그의 전체 포스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 전체 포스트 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 반환 실패")
    })
    @ResponseBody
    @GetMapping("/all/{blogId}")
    public ResponseEntity<PostPagingResponseDto> findTotalPostByBlogId(@PathVariable Long blogId, @ModelAttribute PostSearchDto postSearchDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(PostPagingResponseDto.success(postService.findTotalPaginationPost(blogId, postSearchDto, ConstUtil.TOTAL_POST)));
        } catch (Exception exception) {
            log.error("[freeblog-findTotalPostByBlogId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PostPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "포스트 썸네일 이미지 업로드", description = "포스트 썸네일 이미지 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 포스트 썸네일 이미지 업로드 실패")
    })
    @ResponseBody
    @PostMapping("/upload/post-thumbnail-image")
    public ResponseEntity<String> uploadPostThumbnailImage(@RequestParam("post_thumbnail_image_input") MultipartFile multipartFile, Principal principal) {
        try {
            if (principal == null) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            return ResponseEntity.status(HttpStatus.OK).body(postService.uploadAwsS3PostThumbnailImage(multipartFile));
        } catch (Exception exception) {
            log.error("[freeblog-uploadPostThumbnailImage] exception occurred ", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("포스트 썸네일 이미지 업로드에 실패하였습니다. %s", exception.getMessage()));
        }
    }
}
