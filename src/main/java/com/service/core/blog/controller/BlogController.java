package com.service.core.blog.controller;

import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.category.service.CategoryService;
import com.service.core.comment.service.CommentService;
import com.service.core.tag.dto.BlogTagDto;
import com.service.core.tag.service.TagService;
import com.service.core.user.dto.UserProfileDto;
import com.service.core.user.service.UserService;
import com.service.core.views.service.BlogVisitorService;
import com.service.util.BlogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Tag(name = "블로그", description = "블로그 관련 API")
@RequiredArgsConstructor
@Controller
@RequestMapping("/blog")
@Slf4j
public class BlogController {
    private final BlogService blogService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final TagService tagService;

    private final BlogVisitorService blogVisitorService;

    @Operation(summary = "블로그 페이지 반환", description = "블로그 페이지를 반환하는 GET 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그,사용자 정보가 담긴 블로그 페이지")
    })
    @GetMapping("/{id}")
    public String blog(@PathVariable String id, Model model, Principal principal, HttpServletRequest httpServletRequest) throws Exception {
        if (principal != null) {
            model.addAttribute("user_header", userService.findUserHeaderDtoByEmail(principal.getName()));
        }

        UserProfileDto userProfileDto = userService.findUserProfileDtoById(id);
        model.addAttribute("blog_owner", BlogUtil.checkBlogOwner(principal, userProfileDto.getEmailHash()));
        model.addAttribute("user_profile", userProfileDto);
        model.addAttribute("category", categoryService.findCategoryDtoByUserId(id));
        BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoById(id);
        model.addAttribute("blog_info", blogInfoDto);
        model.addAttribute("recent_comment", commentService.findCommentLinkDto(blogInfoDto.getId()));
        model.addAttribute("blog_tag", BlogTagDto.from(tagService.findTagDtoList(blogInfoDto.getId())));
        // TODO 음악정보, 방문자 수 넘겨줄것
        blogVisitorService.visitBlog(BlogUtil.hashCode(userProfileDto.getId(), userProfileDto.getEmailHash(), blogInfoDto.getId()), BlogUtil.getClientAccessId(httpServletRequest, principal));
        model.addAttribute("blog_visitors", blogVisitorService.getBlogVisitorDto(BlogUtil.hashCode(userProfileDto.getId(), userProfileDto.getEmailHash(), blogInfoDto.getId())));
        return "blog/myblog";
    }
}
