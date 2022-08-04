package com.service.core.email.controller;

import com.service.core.email.service.EmailService;
import com.service.core.error.model.UserAuthException;
import com.service.core.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이메일", description = "이메일 관련 API")
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final UserService userService;

    @Operation(summary = "회원가입 인증 메일 전송", description = "회원가입 인증 메일 전송 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메일 전송 성공"),
            @ApiResponse(responseCode = "500", description = "메일전송 실패")
    })
    @GetMapping(path = "/send/signup")
    public ResponseEntity<String> sendSignUpEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            String key = userService.updateEmailAuthCondition(email);
            emailService.sendSignUpMail(email, key);
            return ResponseEntity.status(HttpStatus.OK).body("이메일 전송에 성공했습니다.");
        } catch (UsernameNotFoundException | MailException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("이메일 전송에 실패하였습니다. %s", exception.getMessage()));
        }
    }

    @Operation(summary = "인증 메일 전송", description = "인증 메일 전송 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메일 전송 성공"),
            @ApiResponse(responseCode = "500", description = "메일전송 실패")
    })
    @GetMapping(path = "/send/auth")
    public ResponseEntity<String> sendAuthEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            String key = userService.updateEmailAuthCondition(email);
            emailService.sendAuthMail(email, key);
            return ResponseEntity.status(HttpStatus.OK).body("이메일 전송에 성공했습니다.");
        } catch (UsernameNotFoundException | MailException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("이메일 전송에 실패하였습니다. %s", exception.getMessage()));
        }
    }

    @Operation(summary = "비밀번호 변경 인증 메일 전송", description = "비밀번호 변경 인증 메일 전송 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 인증, 메일 전송 성공"),
            @ApiResponse(responseCode = "500", description = "인증, 메일전송 오류로 인한 실패")
    })
    @GetMapping(path = "/send/find_password")
    public ResponseEntity<String> sendFindPasswordEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            if (userService.checkIsActive(email)) {
                String key = userService.updatePasswordAuthCondition(email);
                emailService.sendFindPasswordMail(email, key);
            }
            return ResponseEntity.status(HttpStatus.OK).body("이메일 전송에 성공했습니다.");
        } catch (MailException | UserAuthException | UsernameNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("이메일 전송에 실패하였습니다. %s", exception.getMessage()));
        }
    }
}
