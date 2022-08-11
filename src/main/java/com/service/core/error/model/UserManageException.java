package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.util.ConstUtil;

public class UserManageException extends RuntimeException {
    public UserManageException(String message) {
        super(message);
    }

    public UserManageException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
