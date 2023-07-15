package com.service.core.notice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "공지사항", description = "포스트 관련 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

}
