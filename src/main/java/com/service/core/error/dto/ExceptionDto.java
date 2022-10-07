package com.service.core.error.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionDto {
    private final String message;
    private final int statusCode;
}
