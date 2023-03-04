package com.service.core.like.paging;

import lombok.Data;
import lombok.Getter;

@Data
public class LikePaginationResponse<T> {
    private T likeDto;
    private LikePagination likePagination;

    public LikePaginationResponse(T likeDto, LikePagination likePagination) {
        this.likeDto = likeDto;
        this.likePagination = likePagination;
    }
}
