package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;

public class BlogManageException extends BlogServiceException{
    public BlogManageException(String error) {
        super(error);
    }

    public BlogManageException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
