package com.service.core.tag.controller;

import com.service.core.blog.service.BlogService;
import com.service.core.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagRestController {
    private final PostService postService;
    private final BlogService blogService;

    // TODO
}
