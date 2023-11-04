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
    private final String lrc; // TODO lrc 데이터 구하는 것이 어려워 차후 방안 모색 ...
    private final String name;
    private final String url;
    private final long categoryId;
    private final LocalDateTime registerTime;
    private final LocalDateTime updateTime;
}
