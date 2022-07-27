package com.service.core.user.controller;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.User;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.service.UserService;
import com.service.util.ConstUtil;
import com.service.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final BlogService blogService;

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/signup_complete")
    public String complete() {
        return "user/signup_complete";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userSignUpInput", UserSignUpInput.builder().build());
        return "user/signup";
    }

    @GetMapping("/find-info")
    public String findInfo() {
        return "user/find-info";
    }

    @GetMapping("/find-email")
    public String findEmail(
            @RequestParam(value = "nickname", required = false, defaultValue = "") String nickname,
            Model model) {
        // TODO userService findUserByNickname invoke
        return "user/find-email";
    }

    @GetMapping("/email-auth")
    public String emailAuth(
            @RequestParam(value = "email", required = false, defaultValue = "") String email,
            Model model) {
        try {
            userService.emailAuth(email);
        } catch (RuntimeException exception) {
            model.addAttribute("error", String.format("이메일 인증에 실패하였습니다. %s", exception.getMessage()));
        }
        return "user/email_auth";
    }

    @ResponseBody
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            userService.checkSameEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 이메일 입니다.");
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/check-id")
    public ResponseEntity<String> checkId(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        try {
            userService.checkSameId(id);
            return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 id 입니다.");
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @PostMapping("/signup")
    public String signUp(@Valid UserSignUpInput signupForm, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/signup";
            } else if (!signupForm.isEmailCheckFlag() || !signupForm.isIdCheckFlag()) {
                List<String> stringList = new LinkedList<>();

                if (!signupForm.isEmailCheckFlag()) {
                    stringList.add(ConstUtil.UserAuthMessage.NOT_CHECKED_EMAIL);
                }

                if (!signupForm.isIdCheckFlag()) {
                    stringList.add(ConstUtil.UserAuthMessage.NOT_CHECKED_ID);
                }
                throw new UserAuthException(String.join(",", stringList));
            }

            User user = User.from(signupForm);

            if (userService.checkSameUser(user)) {
                Blog blog = blogService.register(Blog.from(signupForm));
                user.setBlog(blog);
                userService.register(user);
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error/signup_fail";
        }

        try {
            emailService.sendMail(signupForm.getEmail());
        } catch (MailException e) {
            model.addAttribute("error", String.format("가입 인증 이메일 전송에 실패하였습니다.  원인: %s", e.getMessage()));
        }
        model.addAttribute("email", signupForm.getEmail());
        return "user/signup_complete";
    }
}
