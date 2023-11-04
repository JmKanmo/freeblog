package com.service.core.main;

import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
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
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Tag(name = "메인화면", description = "메인화면 관련 API")
@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final UserService userService;
    private final BlogService blogService;

    @Operation(summary = "메인 페이지 반환", description = "메인 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자, 최신 블로그 포트스 정보가 담긴 메인 페이지 반환")
    })
    @GetMapping("/")
    public String main(Model model, Principal principal) {
        boolean blog_owner = false;

        if (principal != null) {
            UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
            BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoByEmail(principal.getName());
            UserProfileDto userProfileDto = userService.findUserProfileDtoById(userHeaderDto.getId());
            model.addAttribute("user_header", userHeaderDto);
            model.addAttribute("blog_info", blogInfoDto);
            blog_owner = BlogUtil.checkBlogOwner(principal, userProfileDto.getEmailHash());
        } else {
            model.addAttribute("invisible", true);
        }
        model.addAttribute("blog_owner", blog_owner);
        return "index";
    }

    @Operation(summary = "설정 페이지", description = "설정 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "설정 페이지")
    })
    @GetMapping("/settings")
    public String settings(Model model, Principal principal) {
        boolean blog_owner = false;

        if (principal != null) {
            UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
            BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoByEmail(principal.getName());
            model.addAttribute("blog_info", blogInfoDto);
            model.addAttribute("user_header", userHeaderDto);
            UserProfileDto userProfileDto = userService.findUserProfileDtoById(userHeaderDto.getId());
            blog_owner = BlogUtil.checkBlogOwner(principal, userProfileDto.getEmailHash());
            model.addAttribute("user_setting", userService.findUserSettingDtoByEmail(principal.getName()));
        }
        model.addAttribute("blog_owner", blog_owner);
        return "settings";
    }
}
