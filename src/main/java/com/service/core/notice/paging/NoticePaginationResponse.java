package com.service.core.notice.paging;

import lombok.Data;

@Data
public class NoticePaginationResponse<T> {
    private T noticeDto;
    private NoticePagination noticePagination;

    public NoticePaginationResponse(T noticeDto, NoticePagination noticePagination) {
        this.noticeDto = noticeDto;
        this.noticePagination = noticePagination;
    }
}
