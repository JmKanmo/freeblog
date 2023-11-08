package com.service.core.post.dto;

import com.service.core.main.model.MainPostSearchInput;
import com.service.core.post.paging.PostSearchPagingDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostMainSearchDto {
    private final String keyword;
    private final String searchOption;
    private final String sortOption;
    private final String searchType;
    private PostSearchPagingDto postSearchPagingDto;

    public static PostMainSearchDto from(MainPostSearchInput mainPostSearchInput, PostSearchPagingDto postSearchPagingDto, String searchType) {
        return PostMainSearchDto.builder()
                .keyword(mainPostSearchInput.getKeyword())
                .searchOption(mainPostSearchInput.getSearchOption())
                .sortOption(mainPostSearchInput.getSortOption())
                .searchType(searchType)
                .postSearchPagingDto(postSearchPagingDto)
                .build();
    }
}
