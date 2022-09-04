package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;

public class PostManageException extends RuntimeException {
    public PostManageException(String error) {
        super(error);
    }

    public PostManageException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
