package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;

public class FileHandleException extends BlogServiceException {
    public FileHandleException(String error) {
        super(error);
    }

    public FileHandleException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
