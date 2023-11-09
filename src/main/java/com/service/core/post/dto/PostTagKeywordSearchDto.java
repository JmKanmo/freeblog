package com.service.core.post.dto;


import com.service.core.post.model.BlogPostTagInput;
import com.service.core.post.paging.PostSearchPagingDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostTagKeywordSearchDto {
    private final Long blogId;
    private final String keyword;
    private PostSearchPagingDto postSearchPagingDto;

    public static PostTagKeywordSearchDto from(BlogPostTagInput blogPostTagInput, PostSearchPagingDto postSearchPagingDto) {
        return PostTagKeywordSearchDto.builder()
                .blogId(blogPostTagInput.getBlogId())
                .keyword(blogPostTagInput.getTagKeyword())
                .postSearchPagingDto(postSearchPagingDto)
                .build();
    }
}
