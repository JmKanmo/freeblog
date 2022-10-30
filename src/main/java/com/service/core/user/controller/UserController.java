package com.service.core.user.controller;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.model.*;
import com.service.core.user.service.UserService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import com.service.util.redis.CacheKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
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
    public String login(HttpServletRequest request, Authentication authentication) {
        if (BlogUtil.isAuth(authentication)) {
            return BlogUtil.redirect("/");
        }
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("prevPage", referrer);
        return "user/login";
    }

    @Operation(summary = "회원가입 완료", description = "회원가입 완료 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 완료 페이지")
    })
    @GetMapping("/signup-complete")
    public String complete(Authentication authentication) {
        if (BlogUtil.isAuth(authentication)) {
            return BlogUtil.redirect("/");
        }
        return "user/signup/signup-complete";
    }

    @Operation(summary = "회원가입", description = "회원가입 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 페이지")
    })
    @GetMapping("/signup")
    public String signup(Model model, Authentication authentication) {
        if (BlogUtil.isAuth(authentication)) {
            return BlogUtil.redirect("/");
        }
        model.addAttribute("userSignUpInput", UserSignUpInput.builder().build());
        return "user/signup";
    }

    @Operation(summary = "사용자 정보 찾기", description = "사용자 정보 찾기 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 찾기 페이지")
    })
    @GetMapping("/find-info")
    public String findInfo(Authentication authentication) {
        if (BlogUtil.isAuth(authentication)) {
            return BlogUtil.redirect("/");
        }
        return "user/find/find-info";
    }

    @Operation(summary = "이메일 찾기", description = "이메일 찾기 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자(이메일,닉네임) 목록이 담긴 이메일 찾기 페이지")
    })
    @GetMapping("/find-email")
    public String findEmail(
            @RequestParam(value = "nickname", required = false, defaultValue = "") String nickname,
            Model model, Authentication authentication) {
        if (BlogUtil.isAuth(authentication)) {
            return BlogUtil.redirect("/");
        }
        model.addAttribute("users", userService.findUserEmailFindDtoListByNickname(nickname));
        return "user/find/find-email";
    }

    @Operation(summary = "비밀번호 업데이트 페이지", description = "비밀번호 업데이트 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 업데이트 페이지")
    })
    @GetMapping("/update/password")
    public String updatePassword(Model model) {
        model.addAttribute("userPasswordInput", UserPasswordInput.builder().build());
        return "user/update/password";
    }

    @Operation(summary = "소셜 계정 업데이트 페이지", description = "소셜계정 업데이트 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 업데이트 페이지")
    })
    @GetMapping("/update/social-address")
    public String updateSocialAddress(Model model) {
        model.addAttribute("userSocialAddressInput", UserSocialAddressInput.builder().build());
        return "user/update/social-address";
    }

    @Operation(summary = "소셜 계정 업데이트", description = "소셜계정 업데이트 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 업데이트 페이지")
    })
    @PatchMapping("/update/social-address")
    public String updateSocialAddress(@Valid UserSocialAddressInput userSocialAddressInput, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/update/social-address";
            }
            userService.updateUserSocialAddress(userSocialAddressInput);
            model.addAttribute("result", "소셜 정보 변경 작업 완료. 페이지를 새로고침 후, 변경 사항 확인 가능합니다.");
        } catch (UsernameNotFoundException | UserAuthException exception) {
            model.addAttribute("result", "소셜 정보 변경 작업 실패");
            model.addAttribute("error", String.format("소셜 정보 변경에 실패하였습니다. %s", exception.getMessage()));
        }
        return "user/update/update-info-complete";
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

    @Operation(summary = "사용자 소개 페이지 반환", description = "사용자 소개 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 소개 페이지")
    })
    @GetMapping("/intro")
    public String userIntro() {
        return "user/intro";
    }

    @Operation(summary = "기본 정보 수정 페이지", description = "기본 정보 수정 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기본 정보 수정 페이지")
    })
    @GetMapping("/update/basic-info")
    public String basicInfoEdit(Model model) {
        model.addAttribute("userBasicInfoInput", UserBasicInfoInput.builder().build());
        return "user/update/basic-info";
    }

    @Operation(summary = "기본 정보 수정 작업 진행", description = "기본 정보 수정 작업 진행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기본 정보 수정 작업 성공 페이지"),
            @ApiResponse(responseCode = "500", description = "기본 정보 수정 실패 페이지")
    })
    @PatchMapping("/update/basic-info")
    public String basicInfoUpdate(@Valid UserBasicInfoInput userBasicInfoInput, BindingResult bindingResult, Principal principal, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/update/basic-info";
            } else if (principal == null || principal.getName() == null) {
                throw new UserAuthException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }

            userService.updateUserBasicInfo(userBasicInfoInput, principal);
            model.addAttribute("result", "기본 정보 수정 작업 완료. 페이지를 새로고침 후, 변경 사항 확인 가능합니다.");
        } catch (UsernameNotFoundException exception) {
            model.addAttribute("result", "기본 정보 수정 작업 실패");
            model.addAttribute("error", exception.getMessage());
        }
        return "user/update/update-info-complete";
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
                    stringList.add(ServiceExceptionMessage.NOT_CHECKED_EMAIL.message());
                }

                if (!signupForm.isIdCheckFlag()) {
                    stringList.add(ServiceExceptionMessage.NOT_CHECKED_ID.message());
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
            return "error/signup-fail";
        }
        model.addAttribute("email", signupForm.getEmail());
        return "user/signup/signup-complete";
    }

    @Operation(summary = "회원 탈퇴 페이지 반환", description = "회원 탈퇴 페이지 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 페이지")
    })
    @GetMapping("/withdraw")
    public String withdraw(Model model, Authentication authentication) {
        if (!BlogUtil.isAuth(authentication)) {
            return BlogUtil.redirect("/user/login");
        }
        model.addAttribute("userWithdrawInput", UserWithdrawInput.builder().build());
        return "user/withdraw";
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 진행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 완료"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 작업 실패 등의 오류로 회원 탈퇴 실패")
    })
    @DeleteMapping("/withdraw")
    public String withdraw(@Valid UserWithdrawInput userWithdrawInput, BindingResult bindingResult, Model model, HttpServletRequest httpServletRequest, Authentication authentication) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/withdraw";
            }
            userService.withdraw(userWithdrawInput, authentication);
            model.addAttribute("result", "회원탈퇴 작업 완료");
            httpServletRequest.logout();
        } catch (UsernameNotFoundException | UserAuthException exception) {
            model.addAttribute("result", "회원탈퇴 작업 실패");
            model.addAttribute("error", exception.getMessage());
        } catch (ServletException e) {
            model.addAttribute("result", "회원탈퇴 후 로그아웃 작업 실패");
            model.addAttribute("error", e.getMessage());
        }
        return "user/withdraw/withdraw-complete";
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
    @PatchMapping("/update/password")
    public String updatePassword(@Valid UserPasswordInput userPasswordInput, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "user/update/password";
            }
            userService.updatePassword(userPasswordInput);
            model.addAttribute("result", "비밀번호 변경 작업 완료");
        } catch (UsernameNotFoundException | UserAuthException exception) {
            model.addAttribute("result", "비밀번호 변경 작업 실패");
            model.addAttribute("error", String.format("비밀번호 변경에 실패하였습니다. %s", exception.getMessage()));
        }
        return "user/update/update-password-complete";
    }
}
