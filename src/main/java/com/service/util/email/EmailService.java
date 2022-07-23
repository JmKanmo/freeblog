package com.service.util.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    public long sendMail(String uuid) {
        MimeMessagePreparator msg = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(uuid);
                mimeMessageHelper.setSubject("freelog 가입을 축하드립니다.");
                mimeMessageHelper.setText("<p>freelog 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하셔서 인증을 완료 하세요.</p>"
                                + "<div><a target='_blank' href='http://localhost:8400/user/email-auth?id=" + uuid + "'> 가입 완료 </a></div>"
                        , true);
            }
        };
        javaMailSender.send(msg);
        return 200L;
    }
}
