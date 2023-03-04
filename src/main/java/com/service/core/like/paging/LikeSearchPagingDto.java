package com.service.core.like.paging;

import lombok.Data;

@Data
public class LikeSearchPagingDto {
    private int page;                 // 현재 페이지 번호
    private int recordSize;           // 페이지당 출력할 데이터 개수
    private int pageSize;             // 화면 하단에 출력할 페이지 사이즈

//    private String keyword;           // 검색 키워드
//    private int searchIndex;          // 검색 인덱스
//    private String searchType;        // 검색 유형

    private LikePagination likePagination;

    public LikeSearchPagingDto() {
        this.page = 0;
        this.recordSize = 5;
        this.pageSize = 5;
    }

    public int getRecordSize() {
        return recordSize;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public LikePagination getLikePagination() {
        return likePagination;
    }
}
