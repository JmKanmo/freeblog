package com.service.core.notice.controller;

import com.service.core.blog.dto.BlogDeleteDto;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.notice.service.NoticeService;
import com.service.core.post.model.BlogPostInput;
import com.service.core.user.dto.UserHeaderDto;
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

    @Operation(summary = "공지사항 상세 페이지", description = "공지사항 상세 페이지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 상세 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "공지사항 상세 페이지 반환 실패")
    })
    @GetMapping("/detail/{noticeId}")
    public String noticeDetail(@PathVariable Integer noticeId, Model model) {
        // TODO
        return "notice/notice-detail";
    }

    @Operation(summary = "공지사항 개요 페이지", description = "공지사항 개요 페이지 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 개요 페이지 반환 성공"),
            @ApiResponse(responseCode = "500", description = "공지사항 개요 페이지 반환 실패")
    })
    @GetMapping("/overview")
    public String noticeOverview() {
        // TODO
        return "notice/notice-overview";
    }
}
