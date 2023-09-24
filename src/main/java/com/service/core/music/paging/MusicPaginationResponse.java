package com.service.core.music.paging;

import lombok.Data;

@Data
public class MusicPaginationResponse<T> {
    private T musicDto;
    private MusicPagination musicPagination;

    public MusicPaginationResponse(T musicDto, MusicPagination musicPagination) {
        this.musicDto = musicDto;
        this.musicPagination = musicPagination;
    }
}
