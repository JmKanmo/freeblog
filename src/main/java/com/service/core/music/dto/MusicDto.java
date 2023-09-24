package com.service.core.music.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MusicDto {
    private final long musicId;
    private final String artist;
    private final String cover;
    private final String lrc;
    private final String name;
    private final String url;
    private final String categoryId;
    private final LocalDateTime registerTime;
    private final LocalDateTime updateTime;
}
