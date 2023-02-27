package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;

public class LikeManageException extends BlogServiceException {
    public LikeManageException(String message) {
        super(message);
    }

    public LikeManageException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
