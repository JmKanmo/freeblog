package com.service.util.email;

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
    public Long sendMail(String userId) throws MailException {
        javaMailSender.send(mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(userId);
            mimeMessageHelper.setSubject("freelog 가입을 축하드립니다.");
            mimeMessageHelper.setText("<p>freelog 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하셔서 인증을 완료 하세요.</p>"
                            + "<div><a target='_blank' href='http://localhost:8400/user/email-auth?userId=" + userId + "'> 가입 완료 </a></div>"
                    , true);
        });
        return 200L;
    }
}
