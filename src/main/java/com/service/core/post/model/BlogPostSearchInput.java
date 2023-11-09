package com.service.core.post.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class BlogPostSearchInput {
    @NotEmpty(message = "검색어가 비어있습니다.")
    @NotBlank(message = "검색어는 공백만 올 수 없습니다.")
    private final String keyword;

    @NotNull(message = "검색 옵션이 NULL 입니다.")
    @NotEmpty(message = "검색 옵션은 비어있습니다.")
    @NotBlank(message = "검색 옵션은 공백만 올 수 없습니다.")
    private final String searchOption;

    private final Long blogId;

    public static BlogPostSearchInput from(Long blogId, String keyword, String searchOption) {
        return BlogPostSearchInput.builder()
                .keyword(keyword)
                .searchOption(searchOption)
                .blogId(blogId)
                .build();
    }
}
