package com.service.core.music.controller;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserManageException;
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

import java.security.Principal;

@Tag(name = "뮤직", description = "뮤직 관련 API")
@Controller
@RequestMapping("/music")
@RequiredArgsConstructor
@Slf4j
public class MusicController {
    @Operation(summary = "뮤직 설정 페이지 반환", description = "뮤직 설정 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뮤직 설정 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "뮤직 설정 페이지 반환 실패")
    })
    @GetMapping("/setting")
    public String musicSettingPage(Model model, Principal principal) {
        if ((principal == null || principal.getName() == null)) {
            throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
        }
        return "music/music-setting";
    }

    @Operation(summary = "뮤직 스토어 페이지 반환", description = "뮤직 스토어 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뮤직 스토어 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "뮤직 스토어 페이지 반환 실패")
    })
    @GetMapping("/store")
    public String musicStorePage(Model model, Principal principal) {
        if ((principal == null || principal.getName() == null)) {
            throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
        }
        return "music/music-store";
    }
}
