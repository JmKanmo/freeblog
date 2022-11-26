package com.service.core.post.controller;

import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.service.PostService;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        PostDetailDto postDetailDto = postService.findPostDetailInfo(blogId, postId);
        model.addAttribute("postDetail", postDetailDto);
        model.addAttribute("post_almost", postService.findPostAlmostInfo(postDetailDto.getBlogId(), postDetailDto.getSeq()));
        return "post/post-detail";
    }

    @Operation(summary = "블로그 포스트 작성 페이지 반환", description = "블로그 포스트 작성 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 작성 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "블로그 포스트 작성 페이지 반환 실패")
    })
    @GetMapping("/write/{userId}")
    public String postWritePage(@PathVariable String userId, Model model, Principal principal) {
        if ((principal == null || principal.getName() == null)) {
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
        if ((principal == null || principal.getName() == null)) {
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
        post.setSeq(postService.findPostCountByBlogId(post.getBlog().getId()) + 1);
        postService.register(post, blogPostInput);
        model.addAttribute("result", "게시글 작성이 완료되었습니다. 작성 된 게시글을 확인하려면 페이지를 새로고침 해주세요.");

        return String.format("redirect:/blog/%s", userHeaderDto.getId());
    }
}
