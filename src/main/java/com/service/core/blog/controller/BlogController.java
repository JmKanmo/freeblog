package com.service.core.blog.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "블로그", description = "블로그 관련 API")
@RequiredArgsConstructor
@Controller
@RequestMapping("/blog")
@Slf4j
public class BlogController {
    @Operation(summary = "블로그 페이지 반환", description = "블로그 페이지를 반환하는 GET 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "블로그,사용자 정보가 담긴 블로그 페이지")
    })
    @GetMapping
    public String blog(@RequestParam(value = "id", required = false, defaultValue = ConstUtil.UNDEFINED) String id, Model model) {
        model.addAttribute("id", id);
        return "blog/myblog";
    }
}
