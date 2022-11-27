package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.util.ConstUtil;

public class UserAuthException extends BlogServiceException {
    public UserAuthException(String error) {
        super(error);
    }

    public UserAuthException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
