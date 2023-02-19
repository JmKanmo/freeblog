package com.service.core.post.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class BlogPostSearchInput {
    @NotEmpty(message = "검색어가 비어있습니다.")
    @NotBlank(message = "검색어는 공백만 올 수 없습니다.")
    private final String keyword;

    private final Long blogId;

    public static BlogPostSearchInput from(Long blogId, String keyword) {
        if (keyword.isEmpty()) {
            throw new ValidationException("검색어가 비어있습니다.");
        } else if (keyword.isBlank()) {
            throw new ValidationException("검색어는 공백만 올 수 없습니다.");
        }

        return BlogPostSearchInput.builder()
                .keyword(keyword)
                .blogId(blogId)
                .build();
    }
}
