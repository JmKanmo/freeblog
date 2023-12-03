package com.service.core.music.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMusicConfigDto {
    private final Long userMusicConfigId;
    private final boolean autoPlay;
    private final boolean listFolded;
    private final int listMaxHeight;
    private final int lrcType;
    private final boolean duplicatePlay;
    private final String playOrder;
    private final String playMode;
    private final String loopMode;

    public static UserMusicConfigDto getDefaultUserMusicConifgDto() {
        return UserMusicConfigDto.builder()
                .autoPlay(false)
                .listFolded(true)
                .listMaxHeight(300)
                .lrcType(0)
                .duplicatePlay(false)
                .playOrder("LIST")
                .playMode("FIXED")
                .loopMode("ALL")
                .build();
    }
}
