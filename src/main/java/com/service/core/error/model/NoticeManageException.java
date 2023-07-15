package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;

public class NoticeManageException extends BlogServiceException {
    public NoticeManageException(String error) {
        super(error);
    }

    public NoticeManageException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
