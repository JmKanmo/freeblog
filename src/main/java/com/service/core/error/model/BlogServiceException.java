package com.service.core.error.model;

public abstract class BlogServiceException extends RuntimeException {
    public BlogServiceException(String error) {
        super(error);
    }
}
