package com.service.util;

public class ConstUtil {
    public static final String AUTHENTICATION_MESSAGE = "FLASH_AUTHENTICATION_MESSAGE";
    public static final String UNDEFINED = "<<<undefined>>>";

    public enum ExceptionMessage {
        // 사용자 인증,처리 관련 에러 메시지
        NOT_CHECKED_EMAIL("이메일 중복 검사를 완료하지 않았습니다"),
        AUTH_KEY_NOT_FOUND("인증키가 존재하지 않거나 만료되었습니다. 새로 발급 받아주세요."),
        AUTH_VALID_KEY_MISMATCH("발급 된 인증키 정보가 일치하지 않습니다."),
        RE_PASSWORD_MISMATCH("비밀번호, 재비밀번호 입력 정보가 일치하지 않습니다."),
        COINCIDE_WITH_EACH_PASSWORD("입력한 비밀번호와 변경 전 비밀번호가 일치합니다"),
        NOT_CHECKED_ID("ID 중복 검사를 완료하지 않았습니다."),
        ALREADY_SAME_ID("이미 사용중인 ID 입니다."),
        ALREADY_SAME_EMAIL("이미 사용중인 이메일 입니다."),
        ALREADY_AUTHENTICATED_ACCOUNT("이미 이메일 인증 된 계정 입니다."),
        ACCOUNT_INFO_NOT_FOUND("계정 정보가 존재하지 않습니다."),
        MISMATCH_EMAIL("접속 중인 계정의 이메일 정보와 입력 값이 일치하지 않습니다."),
        MISMATCH_PASSWORD("접속 중인 계정의 비밀번호 정보와 입력 값이 일치하지 않습니다."),
        ID_PW_WRONG("아이디 또는 비밀번호가 틀립니다."),
        LOCK_ACCOUNT("잠긴 계정입니다."),
        DEACTIVATE_ACCOUNT("비활성화된 계정입니다."),
        EXPIRED_ACCOUNT("만료된 계정입니다"),
        EXPIRED_PASSWORD("비밀번호가 만료되었습니다."),
        NOT_AUTHENTICATED_ACCOUNT("이메일 인증이 완료되지 않은 계정 입니다."),
        WITHDRAW_ACCOUNT("탈퇴 된 계정입니다."),
        STOP_ACCOUNT("정지 된 계정입니다."),

        // 기타 처리 관련 에러 메시지
        NOT_VALID_FILE_NAME("파일 명이 유효하지 않습니다.");
        private final String message;

        ExceptionMessage(String message) {
            this.message = message;
        }

        public String message() {
            return message;
        }
    }

    public static final String[] SIGNUP_MAIL_TEXT = {
            "[freelog] 가입을 축하드립니다.",
            "<p>[freelog] 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하고 인증키를 비롯한 정보를 입력하고 이메일 인증을 완료 하세요.</p>"
                    + "<p style='color: mediumpurple; text-decoration: underline;'>24시간 내에 인증을 완료해주세요.</p>"
                    + "<p>인증키: <span style='color:darkgreen; font-weight:bold;'> %s </span> </p>"
                    + "<div><a target='_blank' href='http://localhost:8400/user/email-auth'> 가입 완료 </a></div>"
    };

    public static final String[] AUTH_MAIL_TEXT = {
            "[freelog] 이메일 인증 안내드립니다.",
            "<p>[freelog] 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하고 인증키를 비롯한 정보를 입력하고 이메일 인증을 완료 하세요.</p>"
                    + "<p style='color: mediumpurple; text-decoration: underline;'>24시간 내에 인증을 완료해주세요.</p>"
                    + "<p>인증키: <span style='color:darkgreen; font-weight:bold;'> %s </span> </p>"
                    + "<div><a target='_blank' href='http://localhost:8400/user/email-auth'> 인증 완료 </a></div>"
    };

    public static final String[] FIND_PASSWORD_TEXT = {
            "[freelog] 비밀번호 재발급 안내드립니다.",
            "<p>[freelog] 비밀번호 재발급 안내드립니다.<p><p>아래 링크를 클릭하고 발급키를 비롯한 정보를 입력하고 비밀번호를 재발급 받으세요.</p>"
                    + "<p style='color: mediumpurple; text-decoration: underline;'>24시간 내에 비밀번호 설정을 완료해주세요.</p>"
                    + "<p>발급키: <span style='color:darkgreen; font-weight:bold;'> %s </span> </p>"
                    + "<div><a target='_blank' href='http://localhost:8400/user/update/password'>비밀번호 재발급</a></div>"
    };
}
