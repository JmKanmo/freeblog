package com.service.core.music.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MusicCategoryDto {
    private final Long categoryId;
    private final String name;
    private final LocalDateTime registerTime;
    private final LocalDateTime updateTime;
}
