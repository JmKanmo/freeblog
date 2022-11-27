package com.service.core.category.dto;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class CategoryResponseDto {
    private final String message;
    private final int responseCode;
    private final CategoryDto categoryDto;

    public static CategoryResponseDto success(CategoryDto categoryDto) {
        return CategoryResponseDto.builder()
                .responseCode(HttpStatus.OK.value())
                .categoryDto(categoryDto)
                .message("success")
                .build();
    }

    public static CategoryResponseDto fail(Exception exception) {
        return CategoryResponseDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .categoryDto(null)
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .build();
    }
}
