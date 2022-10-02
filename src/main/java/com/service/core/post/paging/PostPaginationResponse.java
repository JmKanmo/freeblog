package com.service.core.post.paging;

import lombok.Getter;

@Getter
public class PostPaginationResponse<T> {
    private T postTotalDto;
    private PostPagination postPagination;

    public PostPaginationResponse(T postTotalDto, PostPagination postPagination) {
        this.postTotalDto = postTotalDto;
        this.postPagination = postPagination;
    }

}
