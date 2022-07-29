package com.service.core.user.controller;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.User;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.service.UserService;
import com.service.util.ConstUtil;
import com.service.core.email.service.EmailService;
import com.service.util.JmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        return "user/signup/signup_complete";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userSignUpInput", UserSignUpInput.builder().build());
        return "user/signup";
    }

    @GetMapping("/find_info")
    public String findInfo() {
        return "user/find/find_info";
    }

    @GetMapping("/find_email")
    public String findEmail(
            @RequestParam(value = "nickname", required = false, defaultValue = "") String nickname,
            Model model) {
        model.addAttribute("users", userService.findUsersByNickname(nickname));
        return "user/find/find_email";
    }

    @GetMapping("/update_password")
    public String updatePassword(Model model) {
        model.addAttribute("userPasswordInput", UserPasswordInput.builder().build());
        return "user/update/update_password";
    }

    @GetMapping("/email_auth")
    public String emailAuth(Model model) {
        model.addAttribute("userAuthInput", UserAuthInput.builder().build());
        return "user/auth/email_auth";
    }

    @ResponseBody
    @GetMapping("/check_email")
    public ResponseEntity<String> checkEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            userService.checkSameEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 이메일 입니다.");
        } catch (UserManageException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/check_id")
    public ResponseEntity<String> checkId(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        try {
            userService.checkSameId(id);
            return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 id 입니다.");
        } catch (UserManageException exception) {
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

            try {
                emailService.sendSignUpMail(signupForm.getEmail(), user.getAuthKey());
            } catch (MailException e) {
                model.addAttribute("error", String.format("가입 인증 이메일 전송에 실패하였습니다.  원인: %s", e.getMessage()));
            }
        } catch (UserAuthException | UserManageException e) {
            model.addAttribute("error", e.getMessage());
            return "error/signup_fail";
        }
        model.addAttribute("email", signupForm.getEmail());
        return "user/signup/signup_complete";
    }

    @PostMapping("/email_auth")
    public String emailAuth(@Valid UserAuthInput userAuthInput, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/auth/email_auth";
            }
            userService.emailAuth(userAuthInput);
        } catch (UserAuthException | UsernameNotFoundException exception) {
            model.addAttribute("error", String.format("이메일 인증에 실패하였습니다.  원인: %s", exception.getMessage()));
        }
        return "user/auth/email_auth_complete";
    }

    @PostMapping("/update_password")
    public String updatePassword(@Valid UserPasswordInput userPasswordInput, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/update/update_password";
            }
            userService.updatePassword(userPasswordInput);
        } catch (UsernameNotFoundException | UserAuthException exception) {
            model.addAttribute("error", String.format("비밀번호 변경에 실패하였습니다.  원인: %s", exception.getMessage()));
        }
        return "user/update/update_password_complete";
    }
}
