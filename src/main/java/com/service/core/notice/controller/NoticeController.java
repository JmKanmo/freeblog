package com.service.core.notice.controller;

import com.service.core.blog.dto.BlogDeleteDto;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.notice.dto.NoticeDetailDto;
import com.service.core.notice.service.NoticeService;
import com.service.core.post.model.BlogPostInput;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Tag(name = "공지사항", description = "포스트 관련 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;
    private final UserService userService;

    @Operation(summary = "공지사항 상세 페이지", description = "공지사항 상세 페이지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 상세 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "공지사항 상세 페이지 반환 실패")
    })
    @GetMapping("/detail/{noticeId}")
    public String noticeDetail(@PathVariable Long noticeId, Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("user_header", userService.findUserHeaderDtoByEmail(principal.getName()));
        }
        model.addAttribute("invisible", true);
        model.addAttribute("noticeDetail", noticeService.findNoticeDetailDtoById(noticeId));
        return "notice/notice-detail";
    }

    @Operation(summary = "공지사항 개요 페이지", description = "공지사항 개요 페이지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 개요 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "공지사항 개요 페이지 반환 실패")
    })
    @GetMapping("/list")
    public String noticeOverview(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("user_header", userService.findUserHeaderDtoByEmail(principal.getName()));
        }
        model.addAttribute("invisible", true);
        return "notice/notice-list";
    }
}
