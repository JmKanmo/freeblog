package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;

public class MusicManageException extends BlogServiceException {
    public MusicManageException(String error) {
        super(error);
    }

    public MusicManageException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
