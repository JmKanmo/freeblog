package com.service.core.main.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class MainPostSearchInput {
    @NotNull(message = "검색 키워드가 NULL 입니다.")
    private final String keyword;

    @NotNull(message = "검색 옵션이 NULL 입니다.")
    @NotEmpty(message = "검색 옵션이 비어있습니다.")
    @NotBlank(message = "검색 옵션은 공백만 올 수 없습니다")
    private final String searchOption;

    @NotNull(message = "정렬 옵션이 NULL 입니다.")
    @NotEmpty(message = "정렬 옵션이 비어있습니다.")
    @NotBlank(message = "정렬 옵션은 공백만 올 수 없습니다")
    private final String sortOption;
}
