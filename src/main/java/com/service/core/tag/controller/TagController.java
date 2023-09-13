package com.service.core.tag.controller;

import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.dto.UserProfileDto;
import com.service.core.user.service.UserService;
import com.service.util.BlogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {
    private final BlogService blogService;
    private final UserService userService;

    @Operation(summary = "테그 게시글 페이지 반환", description = "특정 태그를 포함한 블로그 페이지를 반환하는 GET 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그,사용자 정보가 담긴 블로그 페이지")
    })
    @GetMapping("/{tagName}")
    public String tagPostPage(@PathVariable String tagName, @RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId, Model model, Principal principal) {
        boolean blog_owner = false;

        if (principal != null) {
            UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
            model.addAttribute("user_header", userHeaderDto);
            UserProfileDto userProfileDto = userService.findUserProfileDtoById(userHeaderDto.getId());
            blog_owner = BlogUtil.checkBlogOwner(principal, userProfileDto.getEmailHash());
        }

        BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoById(blogId);

        model.addAttribute("blog_owner", blog_owner);
        model.addAttribute("blog_info", blogInfoDto);
        model.addAttribute("tagName", tagName);
        return "tag/blog-tag";
    }
}
