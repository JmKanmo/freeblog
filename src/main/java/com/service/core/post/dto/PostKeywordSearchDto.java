package com.service.core.post.dto;

import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.paging.PostSearchPagingDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostKeywordSearchDto {
    private final Long blogId;
    private final String keyword;
    private final String searchType;
    private PostSearchPagingDto postSearchPagingDto;

    public static PostKeywordSearchDto from(BlogPostSearchInput blogPostSearchInput, PostSearchPagingDto postSearchPagingDto, String searchType) {
        return PostKeywordSearchDto.builder()
                .blogId(blogPostSearchInput.getBlogId())
                .keyword(blogPostSearchInput.getKeyword())
                .searchType(searchType)
                .postSearchPagingDto(postSearchPagingDto)
                .build();
    }
}
