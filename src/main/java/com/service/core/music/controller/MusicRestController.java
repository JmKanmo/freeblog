package com.service.core.music.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "뮤직", description = "뮤직 관련 REST API")
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
@Slf4j
public class MusicRestController {

}
