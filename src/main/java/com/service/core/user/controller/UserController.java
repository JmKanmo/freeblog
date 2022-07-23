package com.service.core.user.controller;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.user.domain.User;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.service.UserService;
import com.service.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@Slf4j
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    private final BlogService blogService;

    @GetMapping("/user/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/user/complete")
    public String complete() {
        return "user/complete";
    }

    @GetMapping("/user/signup")
    public String signup(Model model) {
        model.addAttribute("userSignUpInput", UserSignUpInput.builder().build());
        return "user/signup";
    }

    @PostMapping("/user/signup")
    public String signUp(@Valid UserSignUpInput signupForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        try {
            Blog blog = blogService.register(Blog.of(signupForm));
            User user = User.of(signupForm);
            user.setBlog(blog);
            userService.register(user);
        } catch (RuntimeException e) {
            model.addAttribute("error", String.format("회원 가입에 실패하였습니다. 발생 에러: %s", e.getMessage()));
            return "error/signup_fail";
        }

        emailService.sendMail(signupForm.getUserId());
        model.addAttribute("userId", signupForm.getUserId());
        return "user/complete";
    }

    @GetMapping("/member/email-auth")
    public String emailAuth(Model model, @RequestParam(value = "uuid", required = true) String uuid) {
        boolean result = userService.emailAuth(uuid);
        model.addAttribute("result", result);
        return "member/email_auth";
    }
}
