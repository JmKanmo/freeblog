package com.service.core.user.model;

public enum UserStatus {
    /**
     * 미인증
     */
    NOT_AUTH("NOT_AUTH"),

    /**
     * 활성(이용중)
     */
    ACTIVE("ACTIVE"),

    /**
     * 탈퇴
     */
    WITHDRAW("WITHDRAW"),

    /**
     * 정지 된 상태
     */
    STOP("STOP");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }
}
