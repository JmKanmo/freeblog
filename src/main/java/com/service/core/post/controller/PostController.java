package com.service.core.post.controller;

import com.service.core.blog.dto.BlogDeleteDto;
import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogManageException;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.like.service.LikeService;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostUpdateDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.model.BlogPostUpdateInput;
import com.service.core.post.service.PostService;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.dto.UserProfileDto;
import com.service.core.user.service.UserService;
import com.service.util.BlogUtil;
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

import javax.servlet.http.HttpServletRequest;
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
    private final LikeService likeService;

    @Operation(summary = "포스트 상세 페이지 반환", description = "포스트 상세 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포스트 상세 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "포스트 상세 페이지 반환 실패")
    })
    @GetMapping("/{postId}")
    public String postDetailPage(@PathVariable Long postId, @RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId,
                                 Model model, Principal principal,
                                 HttpServletRequest httpServletRequest) throws Exception {
        boolean isEqualPostByLogin = false;

        if (principal != null) {
            model.addAttribute("user_header", userService.findUserHeaderDtoByEmail(principal.getName()));
            BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoByEmail(principal.getName());
            isEqualPostByLogin = postService.checkEqualPostByLogin(blogDeleteDto.getId(), postId);
        }

        UserProfileDto userProfileDto = userService.findUserProfileDtoByBlogId(blogId);
        PostDetailDto postDetailDto = postService.findPostDetailInfo(blogId, postId);
        model.addAttribute("blog_owner", BlogUtil.checkBlogOwner(principal, userProfileDto.getEmailHash()));
        model.addAttribute("isEqualPostByLogin", isEqualPostByLogin);
        model.addAttribute("user_profile", userProfileDto);
        model.addAttribute("postDetail", postDetailDto);
        model.addAttribute("relatedPostList", postService.findRelatedPost(postDetailDto.getId(), postDetailDto.getBlogId(), postDetailDto.getCategoryId(), postDetailDto.getSeq()));
        model.addAttribute("post_almost", postService.findPostAlmostInfo(postDetailDto.getBlogId(), postDetailDto.getSeq()));
        model.addAttribute("post_like", likeService.getPostLikeResultDto(principal, blogId, postId));
        blogService.visitBlog(BlogUtil.hashCode(userProfileDto.getId(), userProfileDto.getEmailHash(), blogId), BlogUtil.getClientAccessId(httpServletRequest, principal));
        model.addAttribute("post_view", postService.viewPost(postDetailDto));
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

        UserProfileDto userProfileDto = userService.findUserProfileDtoById(userId);
        BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoById(userHeaderDto.getId());

        model.addAttribute("user_header", userHeaderDto);
        model.addAttribute("blog_owner", BlogUtil.checkBlogOwner(principal, userProfileDto.getEmailHash()));
        model.addAttribute("blogPostInput", BlogPostInput.builder().id(blogDeleteDto.getId()).build());
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
        BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoById(userHeaderDto.getId());

        if (blogDeleteDto.getId() != blogId) {
            throw new BlogManageException(ServiceExceptionMessage.MISMATCH_BLOG_INFO);
        }

        UserProfileDto userProfileDto = userService.findUserProfileDtoById(userHeaderDto.getId());
        PostUpdateDto postUpdateDto = postService.findPostUpdateInfo(blogId, postId);

        model.addAttribute("user_header", userHeaderDto);
        model.addAttribute("blog_owner", BlogUtil.checkBlogOwner(principal, userProfileDto.getEmailHash()));
        model.addAttribute("blogPostUpdateInput", BlogPostUpdateInput.builder().blogId(blogDeleteDto.getId()).postId(postUpdateDto.getId()).build());
        model.addAttribute("postUpdate", postUpdateDto);
        return "post/post-update";
    }

    @Operation(summary = "키워드 기반 포스트 검색 페이지 반환", description = "키워드 기반 포스트 검색 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 기반 포스트 검색 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "키워드 기반 포스트 검색 페이지 반환 실패")
    })
    @GetMapping("/search")
    public String postSearchPage(@ModelAttribute @Valid BlogPostSearchInput blogPostSearchInput,
                                 Model model, Principal principal) {
        boolean blog_owner = false;

        if (principal != null && principal.getName() != null) {
            UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
            UserProfileDto userProfileDto = userService.findUserProfileDtoById(userHeaderDto.getId());
            blog_owner = BlogUtil.checkBlogOwner(principal, userProfileDto.getEmailHash());
            model.addAttribute("user_header", userHeaderDto);
        }
        model.addAttribute("blog_owner", blog_owner);
        model.addAttribute("blogId", blogPostSearchInput.getBlogId());
        model.addAttribute("keyword", blogPostSearchInput.getKeyword());
        model.addAttribute("searchOption", blogPostSearchInput.getSearchOption());
        return "post/post-search";
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

        BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoByEmail(principal.getName());

        if (blogDeleteDto.getId() != blogPostUpdateInput.getBlogId()) {
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

        BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoByEmail(principal.getName());
        PostDto postDto = postService.findPostDtoById(postId);
        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());

        if (blogDeleteDto.getId() != postDto.getBlogId()) {
            throw new BlogManageException(ServiceExceptionMessage.MISMATCH_BLOG_INFO);
        }

        postService.deletePost(blogDeleteDto.getId(), postId);
        return String.format("redirect:/blog/%s", userHeaderDto.getId());
    }
}
