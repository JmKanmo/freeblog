package com.service.core.email.controller;

import com.service.core.email.service.EmailService;
import com.service.core.error.model.UserAuthException;
import com.service.core.user.service.UserService;
import com.service.util.JmUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final UserService userService;

    @GetMapping(path = "/send/signup")
    public ResponseEntity<String> sendSignUpEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            userService.updateEmailAuthCondition(email);
            emailService.sendSignUpMail(email, JmUtil.createRandomAlphaNumberString(20));
            return ResponseEntity.status(HttpStatus.OK).body("이메일 전송에 성공했습니다.");
        } catch (UsernameNotFoundException | MailException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("이메일 전송에 실패하였습니다.  원인:%s", exception.getMessage()));
        }
    }

    @GetMapping(path = "/send/auth")
    public ResponseEntity<String> sendAuthEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            userService.updateEmailAuthCondition(email);
            emailService.sendAuthMail(email, JmUtil.createRandomAlphaNumberString(20));
            return ResponseEntity.status(HttpStatus.OK).body("이메일 전송에 성공했습니다.");
        } catch (UsernameNotFoundException | MailException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("이메일 전송에 실패하였습니다.  원인:%s", exception.getMessage()));
        }
    }

    @GetMapping(path = "/send/find_password")
    public ResponseEntity<String> sendFindPasswordEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            if (userService.checkIsActive(email)) {
                emailService.sendFindPasswordMail(email, userService.updatePasswordAuthCondition(email));
            }
            return ResponseEntity.status(HttpStatus.OK).body("이메일 전송에 성공했습니다.");
        } catch (MailException | UserAuthException | UsernameNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("이메일 전송에 실패하였습니다.  원인:%s", exception.getMessage()));
        }
    }
}
