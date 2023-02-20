package com.service.core.post.dto;


import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.paging.PostSearchPagingDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostTagKeywordSearchDto {
    private final Long blogId;
    private final String keyword;
    private PostSearchPagingDto postSearchPagingDto;

    public static PostTagKeywordSearchDto from(BlogPostSearchInput blogPostSearchInput, PostSearchPagingDto postSearchPagingDto) {
        return PostTagKeywordSearchDto.builder()
                .blogId(blogPostSearchInput.getBlogId())
                .keyword(blogPostSearchInput.getKeyword())
                .postSearchPagingDto(postSearchPagingDto)
                .build();
    }
}
