package com.service.core.main;

import com.service.core.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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

    @Operation(summary = "메인 페이지 반환", description = "메인 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자, 최신 블로그 포트스 정보가 담긴 메인 페이지 반환")
    })
    @GetMapping("/")
    public String main(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("user", userService.findUserByEmail(principal.getName()));
        }
        return "index";
    }
}
