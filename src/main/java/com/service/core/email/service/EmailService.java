package com.service.core.email.service;

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
    public Long sendSignUpMail(String email, String uuid) throws MailException {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[freelog] 가입을 축하드립니다.");
            mimeMessageHelper.setText("<p>[freelog] 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하셔서 인증키를 비롯한 정보를 입력하고 이메일 인증을 완료 하세요.</p>"
                            + "<p>인증키: " + "<span style='color:darkgreen; font-weight:bold;'>" + uuid + "</span>" + "</p>"
                            + "<div><a target='_blank' href='http://localhost:8400/user/email_auth'> 가입 완료 </a></div>"
                    , true);
        });
        return 200L;
    }

    @Async
    public Long sendAuthMail(String email, String uuid) throws MailException {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[freelog] 이메일 인증 안내드립니다.");
            mimeMessageHelper.setText("<p>[freelog] 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하셔서 인증키를 비롯한 정보를 입력하고 이메일 인증을 완료 하세요.</p>"
                            + "<p>인증키: " + "<span style='color:darkgreen; font-weight:bold;'>" + uuid + "</span>" + "</p>"
                            + "<div><a target='_blank' href='http://localhost:8400/user/email_auth'> 인증 완료 </a></div>"
                    , true);
        });
        return 200L;
    }

    @Async
    public Long sendFindPasswordMail(String email, String uuid) throws MailException {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[freelog] 비밀번호 재발급 안내드립니다.");
            mimeMessageHelper.setText("<p>[freelog] 비밀번호 재발급 안내드립니다.<p><p>아래 링크를 클릭하셔서 발급키를 비롯한 정보를 입력하고 비밀번호를 재발급 받으세요.</p>"
                            + "<p>발급키: " + "<span style='color:darkgreen; font-weight:bold;'>" + uuid + "</span>" + "</p>"
                            + "<div><a target='_blank' href='http://localhost:8400/user/update_password'>비밀번호 재발급</a></div>"
                    , true);
        });
        return 200L;
    }
}
