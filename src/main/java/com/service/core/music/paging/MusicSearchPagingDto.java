package com.service.core.music.paging;

import com.service.core.post.paging.PostPagination;
import lombok.Data;

@Data
public class MusicSearchPagingDto {
    private int page;                 // 현재 페이지 번호
    private int recordSize;           // 페이지당 출력할 데이터 개수
    private int pageSize;             // 화면 하단에 출력할 페이지 사이즈

    private String keyword;           // 검색 키워드
    private String orderBy;           // 정렬 유형
    private String keywordType;       // 검색 키워드 유형 (이름 or 아티스트, etc)
    private String searchType;        // 검색 유형

    private MusicPagination musicPagination;    // 페이지네이션 정보

    public MusicSearchPagingDto() {
        this.page = 0;
        this.recordSize = 10;
        this.pageSize = 10;
    }
}
