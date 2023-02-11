package com.service.core.post.controller;

import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogManageException;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostUpdateDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.model.BlogPostUpdateInput;
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
        boolean isEqualPostByLogin = false;

        if (principal != null) {
            model.addAttribute("user_header", userService.findUserHeaderDtoByEmail(principal.getName()));
            BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoByEmail(principal.getName());
            isEqualPostByLogin = postService.checkEqualPostByLogin(blogInfoDto.getId(), postId);
        }

        model.addAttribute("isEqualPostByLogin", isEqualPostByLogin);
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

    @Operation(summary = "블로그 포스트 수정 페이지 반환", description = "블로그 포스트 수정 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 수정 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "블로그 포스트 수정 페이지 반환 실패")
    })
    @GetMapping("/update/{postId}")
    public String postUpdatePage(@PathVariable Long postId, @RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId,
                                 Model model, Principal principal) {
        if ((principal == null || principal.getName() == null)) {
            throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
        }

        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
        BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoById(userHeaderDto.getId());

        if (blogInfoDto.getId() != blogId) {
            throw new BlogManageException(ServiceExceptionMessage.MISMATCH_BLOG_INFO);
        }
        PostUpdateDto postUpdateDto = postService.findPostUpdateInfo(blogId, postId);

        model.addAttribute("user_header", userHeaderDto);
        model.addAttribute("blogPostUpdateInput", BlogPostUpdateInput.builder().blogId(blogInfoDto.getId()).postId(postUpdateDto.getId()).build());
        model.addAttribute("postUpdate", postUpdateDto);
        return "post/post-update";
    }

    @Operation(summary = "키워드 기반 포스트 검색 페이지 반환", description = "키워드 기반 포스트 검색 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 기반 포스트 검색 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "키워드 기반 포스트 검색 페이지 반환 실패")
    })
    @GetMapping("/search")
    public String postSearchPage(@RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId,
                                 @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model, Principal principal) {
        if (principal != null && principal.getName() != null) {
            UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
            model.addAttribute("user_header", userHeaderDto);
        }
        model.addAttribute("blogId", blogId);
        model.addAttribute("keyword", keyword);
        return "post/post-search";
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
        BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoByEmail(principal.getName());

        if (blogInfoDto.getId() != blogPostInput.getId()) {
            throw new BlogManageException(ServiceExceptionMessage.MISMATCH_BLOG_INFO);
        }
        post.setWriter(userHeaderDto.getNickname());
        post.setBlog(blogService.findBlogByEmail(principal.getName()));
        post.setCategory(categoryService.findCategoryById(principal.getName(), blogPostInput.getCategory()));
        post.setSeq((long) (postService.findPostCountByBlogId(post.getBlog().getId()) + 1));
        postService.register(post, blogPostInput);
        model.addAttribute("result", "게시글 작성이 완료되었습니다. 작성 된 게시글을 확인하려면 페이지를 새로고침 해주세요.");

        return String.format("redirect:/blog/%s", userHeaderDto.getId());
    }

    @Operation(summary = "블로그 포스트 수정 작업", description = "블로그 포스트 수정 작업 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 수정 작업 성공"),
            @ApiResponse(responseCode = "500", description = "DB 연결 오류, SQL 쿼리 수행 실패 등의 이유로 블로그 포스트 수정 작업 실패")
    })
    @PostMapping("/update")
    public String postUpdate(@Valid BlogPostUpdateInput blogPostUpdateInput, BindingResult bindingResult, Model model, Principal principal) {
        if ((principal == null || principal.getName() == null)) {
            throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
        }

        if (bindingResult.hasErrors()) {
            return "post/post-update";
        }

        BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoByEmail(principal.getName());

        if (blogInfoDto.getId() != blogPostUpdateInput.getBlogId()) {
            throw new BlogManageException(ServiceExceptionMessage.MISMATCH_BLOG_INFO);
        }

        postService.update(blogPostUpdateInput, categoryService);
        return String.format("redirect:/post/%d?blogId=%d", blogPostUpdateInput.getPostId(), blogPostUpdateInput.getBlogId());
    }

    @Operation(summary = "블로그 포스트 삭제 작업", description = "블로그 포스트 삭제 작업 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그 포스트 삭제 작업 성공"),
            @ApiResponse(responseCode = "500", description = "DB 연결 오류, SQL 쿼리 수행 실패 등의 이유로 블로그 포스트 삭제 작업 실패")
    })
    @PostMapping("/delete")
    public String postDelete(@RequestParam(value = "postId", required = false, defaultValue = "0") Long postId, Principal principal) {
        if ((principal == null || principal.getName() == null)) {
            throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
        }

        BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoByEmail(principal.getName());
        PostDto postDto = postService.findPostDtoById(postId);

        if (blogInfoDto.getId() != postDto.getBlogId()) {
            throw new BlogManageException(ServiceExceptionMessage.MISMATCH_BLOG_INFO);
        }

        postService.deletePost(postId);
        return String.format("redirect:/blog/%s", blogInfoDto.getName());
    }
}
