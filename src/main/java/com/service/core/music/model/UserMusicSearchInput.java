package com.service.core.music.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMusicSearchInput {
    private final long blogId;
    private long categoryId;
    private final String orderBy;
}
