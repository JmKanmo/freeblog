package com.service.util.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    @PostMapping(path = "/send_email")
    public ResponseEntity<Long> sendEmail(EmailInput emailInput) {
        return ResponseEntity.status(HttpStatus.OK).body(emailService.sendMail(emailInput.getSubject()));
    }
}
