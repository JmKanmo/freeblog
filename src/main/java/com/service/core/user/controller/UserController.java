package com.service.core.user.controller;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.service.UserAuthService;
import com.service.core.user.service.UserService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import com.service.core.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.LinkedList;
import java.util.List;

@Tag(name = "사용자", description = "사용자 관련 API")
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "로그인 페이지", description = "로그인 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 페이지")
    })
    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("prevPage", referrer);
        return "user/login";
    }

    @Operation(summary = "회원가입 완료", description = "회원가입 완료 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 완료 페이지")
    })
    @GetMapping("/signup-complete")
    public String complete() {
        return "user/signup/signup-complete";
    }

    @Operation(summary = "회원가입", description = "회원가입 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 페이지")
    })
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userSignUpInput", UserSignUpInput.builder().build());
        return "user/signup";
    }

    @Operation(summary = "사용자 정보 찾기", description = "사용자 정보 찾기 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 찾기 페이지")
    })
    @GetMapping("/find-info")
    public String findInfo() {
        return "user/find/find-info";
    }

    @Operation(summary = "이메일 찾기", description = "이메일 찾기 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자(이메일,닉네임) 목록이 담긴 이메일 찾기 페이지")
    })
    @GetMapping("/find-email")
    public String findEmail(
            @RequestParam(value = "nickname", required = false, defaultValue = "") String nickname,
            Model model) {
        model.addAttribute("users", userService.findUsersByNickname(nickname));
        return "user/find/find-email";
    }

    @Operation(summary = "비밀번호 업데이트", description = "비밀번호 업데이트 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 업데이트 페이지")
    })
    @GetMapping("/update-password")
    public String updatePassword(Model model) {
        model.addAttribute("userPasswordInput", UserPasswordInput.builder().build());
        return "user/update/update-password";
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 페이지")
    })
    @GetMapping("/email-auth")
    public String emailAuth(Model model) {
        model.addAttribute("userAuthInput", UserAuthInput.builder().build());
        return "user/auth/email-auth";
    }

    @Operation(summary = "이메일 사용가능 여부 확인", description = "이메일 사용 가능 여부 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 사용 가능"),
            @ApiResponse(responseCode = "500", description = "이미 사용 중으로 불가능")
    })
    @ResponseBody
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        userService.checkSameEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 이메일 입니다.");
    }

    @Operation(summary = "id 사용가능 여부 확인", description = "id 사용 가능 여부 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "id 사용 가능"),
            @ApiResponse(responseCode = "500", description = "이미 사용 중으로 불가능")
    })
    @ResponseBody
    @GetMapping("/check-id")
    public ResponseEntity<String> checkId(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        userService.checkSameId(id);
        return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 id 입니다.");
    }

    @Operation(summary = "회원가입", description = "회원가입 진행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 완료"),
            @ApiResponse(responseCode = "500", description = "이메일 or id 인증 오류, 중복 가입 시도, 데이터베이스 오류 등으로 회원가입 실패")
    })
    @PostMapping("/signup")
    public String signUp(@Valid UserSignUpInput signupForm, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/signup";
            } else if (!signupForm.isEmailCheckFlag() || !signupForm.isIdCheckFlag()) {
                List<String> stringList = new LinkedList<>();

                if (!signupForm.isEmailCheckFlag()) {
                    stringList.add(ConstUtil.ExceptionMessage.NOT_CHECKED_EMAIL.message());
                }

                if (!signupForm.isIdCheckFlag()) {
                    stringList.add(ConstUtil.ExceptionMessage.NOT_CHECKED_ID.message());
                }
                throw new UserAuthException(String.join(",", stringList));
            }

            try {
                userService.processSignUp(signupForm, UserDomain.from(signupForm));
            } catch (MailException mailException) {
                model.addAttribute("error", String.format("가입 인증 이메일 전송에 실패하였습니다. %s", mailException.getMessage()));
            }
        } catch (UserAuthException | UserManageException e) {
            model.addAttribute("error", e.getMessage());
            return "error/signup_fail";
        }
        model.addAttribute("email", signupForm.getEmail());
        return "user/signup/signup-complete";
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증 진행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 완료"),
            @ApiResponse(responseCode = "500", description = "이메일 or 발급키 인증 오류로 인증 실패")
    })
    @PostMapping("/email-auth")
    public String emailAuth(@Valid UserAuthInput userAuthInput, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/auth/email-auth";
            }
            userService.emailAuth(userAuthInput);
        } catch (UserAuthException | UsernameNotFoundException exception) {
            model.addAttribute("error", String.format("이메일 인증에 실패하였습니다. %s", exception.getMessage()));
        }
        return "user/auth/email-auth-complete";
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경 진행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 완료"),
            @ApiResponse(responseCode = "500", description = "이메일 or 인증키 or 비밀번호 인증 오류로 비밀번호 변경 실패")
    })
    @PostMapping("/update-password")
    public String updatePassword(@Valid UserPasswordInput userPasswordInput, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/update/update-password";
            }
            userService.updatePassword(userPasswordInput);
        } catch (UsernameNotFoundException | UserAuthException exception) {
            model.addAttribute("error", String.format("비밀번호 변경에 실패하였습니다. %s", exception.getMessage()));
        }
        return "user/update/update-password-complete";
    }
}
