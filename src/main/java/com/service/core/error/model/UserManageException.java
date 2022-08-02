package com.service.core.error.model;

import com.service.util.ConstUtil;

public class UserManageException extends RuntimeException {
    public UserManageException(String message) {
        super(message);
    }

    public UserManageException(ConstUtil.ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
