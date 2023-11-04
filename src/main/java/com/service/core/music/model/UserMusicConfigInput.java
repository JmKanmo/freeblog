package com.service.core.music.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserMusicConfigInput {
    private final boolean listFolded;
    @Min(0)
    private final int listMaxHeight;
    private final boolean autoPlay;
    private final boolean duplicatePlay;
    @NotNull(message = "재생 순서가 NULL 입니다.")
    @NotEmpty(message = "재생 순서가 비어있습니다.")
    @NotBlank(message = "재생 순서는 공백만 올 수 없습니다")
    private final String playOrder;
    @NotNull(message = "재생 모드가 NULL 입니다.")
    @NotEmpty(message = "재생 모드가 비어있습니다.")
    @NotBlank(message = "재생 모드는 공백만 올 수 없습니다")
    private final String playMode;
}
