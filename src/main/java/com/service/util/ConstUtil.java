package com.service.util;

public class ConstUtil {
    public static final String AUTHENTICATION_MESSAGE = "FLASH_AUTHENTICATION_MESSAGE";
    public static final String UNDEFINED = "<<<undefined>>>";
    public static final String NOT_EXIST_CATEGORY = "분류 없음";
    public static final String TOTAL_CATEGORY = "전체 카테고리";
    public static final String TOTAL_POST = "전체글";
    public static final String SFTP_IMAGE_URL = "https://%s/images/jmblog/%s";

    public static final String AWS_S3_IMAGE_URL = "https://freelog-s3-bucket.s3.amazonaws.com/image/%s";
    public static final String[] SIGNUP_MAIL_TEXT = {
            "[freelog] 가입을 축하드립니다.",
            "<p>[freelog] 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하고 인증키를 비롯한 정보를 입력하고 이메일 인증을 완료 하세요.</p>"
                    + "<p style='color: mediumpurple; text-decoration: underline;'>24시간 내에 인증을 완료해주세요.</p>"
                    + "<p>인증키: <span style='color:darkgreen; font-weight:bold;'> %s </span> </p>"
                    + "<div><a target='_blank' href='https://localhost:8400/user/email-auth'> 가입 완료 </a></div>"
    };

    public static final String[] AUTH_MAIL_TEXT = {
            "[freelog] 이메일 인증 안내드립니다.",
            "<p>[freelog] 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하고 인증키를 비롯한 정보를 입력하고 이메일 인증을 완료 하세요.</p>"
                    + "<p style='color: mediumpurple; text-decoration: underline;'>24시간 내에 인증을 완료해주세요.</p>"
                    + "<p>인증키: <span style='color:darkgreen; font-weight:bold;'> %s </span> </p>"
                    + "<div><a target='_blank' href='https://localhost:8400/user/email-auth'> 인증 완료 </a></div>"
    };

    public static final String[] FIND_PASSWORD_TEXT = {
            "[freelog] 비밀번호 재발급 안내드립니다.",
            "<p>[freelog] 비밀번호 재발급 안내드립니다.<p><p>아래 링크를 클릭하고 발급키를 비롯한 정보를 입력하고 비밀번호를 재발급 받으세요.</p>"
                    + "<p style='color: mediumpurple; text-decoration: underline;'>24시간 내에 비밀번호 설정을 완료해주세요.</p>"
                    + "<p>발급키: <span style='color:darkgreen; font-weight:bold;'> %s </span> </p>"
                    + "<div><a target='_blank' href='https://localhost:8400/user/update/password'>비밀번호 재발급</a></div>"
    };
}
