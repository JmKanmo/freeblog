package com.service.core.error.controller;

import com.service.util.JmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;


@RequiredArgsConstructor
@Controller
@Slf4j
public class ErrorController {
    @GetMapping("/error/denied")
    public String errorDenied() {
        return "error/denied";
    }

    @GetMapping("/error/login_fail")
    public String accessFail(@ModelAttribute(JmUtil.AUTHENTICATION_MESSAGE) final String authenticationMessage, final Model model) {
        model.addAttribute("error", authenticationMessage);
        return "error/login_fail";
    }
}
