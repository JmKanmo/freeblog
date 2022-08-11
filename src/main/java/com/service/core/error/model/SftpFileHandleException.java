package com.service.core.error.model;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.util.ConstUtil;

public class SftpFileHandleException extends RuntimeException {
    public SftpFileHandleException(String error) {
        super(error);
    }

    public SftpFileHandleException(ServiceExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
