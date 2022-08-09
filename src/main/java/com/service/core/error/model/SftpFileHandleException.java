package com.service.core.error.model;

import com.service.util.ConstUtil;

public class SftpFileHandleException extends RuntimeException {
    public SftpFileHandleException(String error) {
        super(error);
    }

    public SftpFileHandleException(ConstUtil.ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
