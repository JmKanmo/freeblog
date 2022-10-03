package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;

public class CommentManageException extends RuntimeException {
    public CommentManageException(String error) {
        super(error);
    }

    public CommentManageException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
