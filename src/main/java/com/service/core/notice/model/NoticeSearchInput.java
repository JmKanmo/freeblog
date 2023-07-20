package com.service.core.notice.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class NoticeSearchInput {
    @NotEmpty(message = "검색어가 비어있습니다.")
    @NotBlank(message = "검색어는 공백만 올 수 없습니다.")
    private final String keyword;

    public static NoticeSearchInput from(String keyword) {
        return NoticeSearchInput.builder()
                .keyword(keyword)
                .build();
    }
}
