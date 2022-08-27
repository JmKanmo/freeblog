package com.service.core.blog.controller;

import com.service.core.category.service.CategoryService;
import com.service.core.user.dto.UserBasicDto;
import com.service.core.user.service.UserService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Tag(name = "블로그", description = "블로그 관련 API")
@RequiredArgsConstructor
@Controller
@RequestMapping("/blog")
@Slf4j
public class BlogController {
    private final UserService userService;
    private final CategoryService categoryService;

    @Operation(summary = "블로그 페이지 반환", description = "블로그 페이지를 반환하는 GET 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그,사용자 정보가 담긴 블로그 페이지")
    })
    @GetMapping("/{id}")
    public String blog(@PathVariable String id, Model model, Principal principal) {
        if (principal != null) {
            UserBasicDto userBasicDto = userService.findUserBasicDtoByEmail(principal.getName());
            model.addAttribute("user_basic", userBasicDto);
            model.addAttribute("blog_owner", BlogUtil.checkBlogOwner(principal, userBasicDto.getEmailHash()));
        }
        model.addAttribute("category", categoryService.findCategoryDto(id));
        // TODO 최신글, 인기글, 태그, 방문자수, 음악정보, 소개, 최신 게시글 ... 정보 넘겨줄것
        return "blog/myblog";
    }
}
