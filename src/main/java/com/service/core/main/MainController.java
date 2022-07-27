package com.service.core.main;

import com.service.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final UserService userService;

    @GetMapping("/")
    public String main(Model model, Principal principal) {
        try {
            if (principal != null) {
                model.addAttribute("user", userService.findUserByEmail(principal.getName()));
            }
        } catch (RuntimeException runtimeException) {
            log.error("[JmBlog:MainController-main] error happened => ${}", runtimeException.getMessage());
        }
        return "index";
    }
}
