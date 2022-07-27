package com.service.core.blog.controller;

import com.service.util.ConstUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/blog")
@Slf4j
public class BlogController {
    @GetMapping
    public String blog(@RequestParam(value = "id", required = false, defaultValue = ConstUtil.UNDEFINED) String id, Model model) {
        model.addAttribute("id", id);
        return "blog/myblog";
    }

}
