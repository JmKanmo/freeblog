package com.service.core.music.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMusicInput {
    private final Long musicId;
    private final Long musicCategoryId;
    private final String title;
    private final String artist;
    private final String url;
    private final String cover;
    private final String lrc;
}
