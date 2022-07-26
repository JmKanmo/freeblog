package com.service.util.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @GetMapping(path = "/email/send")
    public ResponseEntity<String> sendEmail(@RequestParam(value = "userId", required = false, defaultValue = "") String userId) {
        try {
            emailService.sendMail(userId);
            return ResponseEntity.status(HttpStatus.OK).body("이메일 전송해 성공했습니다.");
        } catch (MailException mailException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("이메일 전송해 실패하였습니다. 원인:%s", mailException.getMessage()));
        }
    }
}
