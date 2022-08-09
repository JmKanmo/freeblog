package com.service.core.error.controller;

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
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "오류", description = "오류 관련 API")
@RequiredArgsConstructor
@Controller
@Slf4j
public class ErrorController {
    @Operation(summary = "접근 불가 에러 페이지", description = "접근 불가 에러 페이지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "접근 불가 페이지 반환")
    })
    @GetMapping("/error/denied")
    public String errorDenied() {
        return "error/denied";
    }

    @Operation(summary = "로그인 오류 페이지", description = "로그인 오류 페이지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "에러 메시지가 담긴 로그인 오류 페이지 반환")
    })
    @GetMapping("/error/login-fail")
    public String accessFail(@ModelAttribute(ConstUtil.AUTHENTICATION_MESSAGE) final String authenticationMessage, final Model model) {
        model.addAttribute("error", authenticationMessage);
        return "error/login-fail";
    }
}
