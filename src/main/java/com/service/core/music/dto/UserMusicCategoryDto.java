package com.service.core.music.dto;

import com.service.core.music.domain.UserMusicCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserMusicCategoryDto {
    private final Long id;
    private final Long targetId;
    private final String name;
    private final LocalDateTime registerTime;
    private final LocalDateTime updateTime;

    public static UserMusicCategoryDto from(UserMusicCategory userMusicCategory) {
        return UserMusicCategoryDto.builder()
                .id(userMusicCategory.getId())
                .targetId(userMusicCategory.getTargetId())
                .name(userMusicCategory.getName())
                .registerTime(userMusicCategory.getRegisterTime())
                .updateTime(userMusicCategory.getUpdateTime())
                .build();
    }
}
