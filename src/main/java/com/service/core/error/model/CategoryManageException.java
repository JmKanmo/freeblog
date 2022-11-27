package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;

public class CategoryManageException extends BlogServiceException {
    public CategoryManageException(String error) {
        super(error);
    }

    public CategoryManageException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
