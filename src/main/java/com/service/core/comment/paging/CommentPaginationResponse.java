package com.service.core.comment.paging;

import lombok.Data;

@Data
public class CommentPaginationResponse<T> {
    private T commentSummaryDto;
    private CommentPagination commentPagination;

    public CommentPaginationResponse(T commentSummaryDto, CommentPagination commentPagination) {
        this.commentSummaryDto = commentSummaryDto;
        this.commentPagination = commentPagination;
    }
}
