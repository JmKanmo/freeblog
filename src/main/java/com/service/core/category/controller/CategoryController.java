package com.service.core.category.controller;

import com.service.core.blog.dto.BlogDeleteDto;
import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogManageException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Tag(name = "카테고리", description = "카테고리 관련 API")
@RequiredArgsConstructor
@Controller
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    private final BlogService blogService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 설정 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "카테고리 설정 페이지 반환 실패")
    })
    @GetMapping("/setting")
    public String categorySettingPage(@RequestParam(value = "blogId", required = false, defaultValue = "0") Long blogId, Model model, Principal principal) {
        if ((principal == null || principal.getName() == null)) {
            throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
        }

        BlogDeleteDto blogDeleteDto = blogService.findBlogDeleteDtoByEmail(principal.getName());

        if (blogDeleteDto.getId() != blogId) {
            throw new BlogManageException(ServiceExceptionMessage.MISMATCH_BLOG_INFO);
        }

        model.addAttribute("blogId", blogId);
        model.addAttribute("category", categoryService.findCategoryDtoByBlogId(blogId));
        return "category/category-setting";
    }
}
