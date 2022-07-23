package com.service.core.error.model;

public class UserAuthException extends RuntimeException {
    public UserAuthException(String error) {
        super(error);
    }
}
