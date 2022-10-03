package com.service.core.comment.paging;

import lombok.Getter;

@Getter
public class CommentPaginationResponse<T> {
    private T commentTotalDto;
    private CommentPagination commentPagination;

    public CommentPaginationResponse(T commentTotalDto, CommentPagination commentPagination) {
        this.commentTotalDto = commentTotalDto;
        this.commentPagination = commentPagination;
    }
}
