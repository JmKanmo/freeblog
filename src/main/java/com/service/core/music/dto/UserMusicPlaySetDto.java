package com.service.core.music.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserMusicPlaySetDto {
    private final List<UserMusicDto> userMusicDtoList;
    private final UserMusicConfigDto userMusicConfigDto;

    public static UserMusicPlaySetDto from(List<UserMusicDto> userMusicDtoList, UserMusicConfigDto userMusicConfigDto) {
        return UserMusicPlaySetDto.builder()
                .userMusicDtoList(userMusicDtoList)
                .userMusicConfigDto(userMusicConfigDto)
                .build();
    }
}
