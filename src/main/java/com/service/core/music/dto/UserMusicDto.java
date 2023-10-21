package com.service.core.music.dto;

import com.service.core.music.domain.UserMusic;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserMusicDto {
    private final long musicId;
    private final String artist;
    private final String cover;
    private final String lrc;
    private final String name;
    private final String url;
    private final int hashCode;
    private final long categoryId;
    private final LocalDateTime registerTime;
    private final LocalDateTime updateTime;
}
