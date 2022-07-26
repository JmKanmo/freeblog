package com.service.core.user.controller;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.error.model.UserAuthException;
import com.service.core.user.domain.User;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.service.UserService;
import com.service.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/user/signup_complete")
    public String complete() {
        return "user/signup_complete";
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
            User user = User.of(signupForm);

            if (userService.checkSameUser(user)) {
                Blog blog = blogService.register(Blog.of(signupForm));
                user.setBlog(blog);
                userService.register(user);
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", String.format("회원 가입에 실패하였습니다. 원인: %s", e.getMessage()));
            return "error/signup_fail";
        }

        try {
            emailService.sendMail(signupForm.getUserId());
        } catch (MailException e) {
            model.addAttribute("error", String.format("가입 인증 이메일 전송에 실패하였습니다. 원인: %s", e.getMessage()));
        }
        model.addAttribute("userId", signupForm.getUserId());
        return "user/signup_complete";
    }

    @GetMapping("/user/email-auth")
    public String emailAuth(
            @RequestParam(value = "userId", required = false, defaultValue = "") String userId,
            Model model) {
        try {
            userService.emailAuth(userId);
        } catch (UserAuthException exception) {
            model.addAttribute("error", String.format("이메일 인증에 실패하였습니다. %s", exception.getMessage()));
        }
        return "user/email_auth";
    }
}
