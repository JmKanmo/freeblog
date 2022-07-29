package com.service.util;

public class ConstUtil {
    public static final String AUTHENTICATION_MESSAGE = "FLASH_AUTHENTICATION_MESSAGE";
    public static final String UNDEFINED = "<<<undefined>>>";

    // auth const variable
    public static class UserAuthMessage {
        public static final String NOT_CHECKED_EMAIL = "이메일 중복 검사를 완료하지 않았습니다";
        public static final String AUTH_VALID_TIME_EXPIRED = "인증 유효 시간이 지나 만료되었습니다.";
        public static final String AUTH_VALID_KEY_MISMATCH = "발급 된 인증키 정보가 불일치합니다.";
        public static final String RE_PASSWORD_MISMATCH = "재비밀번호 입력 정보가 일치하지 않습니다.";
        public static final String NOT_CHECKED_ID = "ID 중복 검사를 완료하지 않았습니다.";
        public static final String ALREADY_SAME_ID = "이미 사용중인 ID 입니다.";
        public static final String ALREADY_SAME_EMAIL = "이미 사용중인 이메일 입니다.";
        public static final String ALREADY_AUTHENTICATED_USER = "이미 이메일 인증 된 유저 입니다.";
        public static final String USER_INFO_NOT_FOUND = "회원 정보가 존재하지 않습니다.";
        public static final String ID_PW_WRONG = "아이디 또는 비밀번호가 틀립니다.";
        public static final String LOCK_ACCOUNT = "잠긴 계정입니다.";
        public static final String DEACTIVATE_ACCOUNT = "비활성화된 계정입니다.";
        public static final String EXPIRED_ACCOUNT = "만료된 계정입니다";
        public static final String EXPIRED_PASSWORD = "비밀번호가 만료되었습니다.";
        public static final String NOT_AUTHENTICATED_USER = "이메일 인증이 완료되지 않은 회원입니다.";
        public static final String WITHDRAW_USER = "탈퇴 된 회원입니다.";
        public static final String STOP_USER = "정지 된 회원입니다.";
    }
}
