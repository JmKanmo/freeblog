package com.service.core.category.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class CategoryInput {
    private final Long id;
    private final Long seq;

    @NotEmpty(message = "카테고리명이 비어있습니다.")
    @NotBlank(message = "카테고리명은 공백만 올 수 없습니다")
    @Size(max = 25, message = "카테고리명은 최대 25글자 까지 작성 가능합니다.")
    private final String name;

    @NotEmpty(message = "카테고리 타입이 비어있습니다.")
    @NotBlank(message = "카테고리  타입은 공백만 올 수 없습니다")
    private final String type; // parent | child
}
