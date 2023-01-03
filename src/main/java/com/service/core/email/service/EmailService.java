package com.service.core.email.service;

import com.service.util.ConstUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Async
    public Long sendSignUpMail(String email, String uuid, String httpMethod, String ip, int port) throws MailException {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(ConstUtil.SIGNUP_MAIL_TEXT[0]);
            mimeMessageHelper.setText(String.format(ConstUtil.SIGNUP_MAIL_TEXT[1], uuid, httpMethod, ip, port), true);
        });
        return 200L;
    }

    @Async
    public Long sendAuthMail(String email, String uuid, String httpMethod, String ip, int port) throws MailException {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(ConstUtil.AUTH_MAIL_TEXT[0]);
            mimeMessageHelper.setText(String.format(ConstUtil.AUTH_MAIL_TEXT[1], uuid, httpMethod, ip, port), true);
        });
        return 200L;
    }

    @Async
    public Long sendFindPasswordMail(String email, String uuid, String httpMethod, String ip, int port) throws MailException {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(ConstUtil.FIND_PASSWORD_TEXT[0]);
            mimeMessageHelper.setText(String.format(ConstUtil.FIND_PASSWORD_TEXT[1], uuid, httpMethod, ip, port), true);
        });
        return 200L;
    }
}
