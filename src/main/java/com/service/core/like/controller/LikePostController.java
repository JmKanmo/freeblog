package com.service.core.like.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "좋아요", description = "좋아요 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
@Slf4j
public class LikePostController {

}
