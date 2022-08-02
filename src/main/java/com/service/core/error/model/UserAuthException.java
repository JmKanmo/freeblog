package com.service.core.error.model;

import com.service.util.ConstUtil;

public class UserAuthException extends RuntimeException {
    public UserAuthException(String error) {
        super(error);
    }

    public UserAuthException(ConstUtil.ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
