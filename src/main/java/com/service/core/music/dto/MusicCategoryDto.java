package com.service.core.music.dto;

import com.service.core.music.domain.MusicCategory;
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
    private final Boolean isBaseTimezone;

    public static MusicCategoryDto from(MusicCategory musicCategory) {
        return MusicCategoryDto.builder()
                .categoryId(musicCategory.getId())
                .name(musicCategory.getName())
                .registerTime(musicCategory.getRegisterTime())
                .updateTime(musicCategory.getUpdateTime())
                .isBaseTimezone(musicCategory.getIsBaseTimezone())
                .build();
    }
}
