package com.service.util.paging;

import lombok.Getter;

@Getter
public class PaginationResponse<T> {
    private T postTotalDto;
    private Pagination pagination;

    public PaginationResponse(T postTotalDto, Pagination pagination) {
        this.postTotalDto = postTotalDto;
        this.pagination = pagination;
    }

}
