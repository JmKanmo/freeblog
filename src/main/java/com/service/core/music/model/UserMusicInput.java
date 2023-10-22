package com.service.core.music.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserMusicInput {
    private final Long musicId;
    private final Long musicCategoryId;

    @NotEmpty(message = "뮤직명이 비어있습니다.")
    @NotBlank(message = "뮤직명은 공백만 올 수 없습니다.")
    @Size(max = 50, message = "뮤직명은 최대 50글자 까지 작성 가능합니다.")
    private final String title;

    @NotEmpty(message = "뮤직 아티스트명이 비어있습니다.")
    @NotBlank(message = "뮤직 아티스트명은 공백만 올 수 없습니다.")
    @Size(max = 50, message = "뮤직 아티스트명은 최대 50글자 까지 작성 가능합니다.")
    private final String artist;

    @NotEmpty(message = "뮤직 URL이 비어있습니다.")
    @NotBlank(message = "뮤직 URL은 공백만 올 수 없습니다.")
    @Size(max = 300, message = "뮤직 URL은 최대 300글자 까지 작성 가능합니다.")
    private final String url;

    @NotEmpty(message = "뮤직 COVER이 비어있습니다.")
    @NotBlank(message = "뮤직 COVER은 공백만 올 수 없습니다.")
    @Size(max = 300, message = "뮤직 COVER은 최대 300글자 까지 작성 가능합니다.")
    private final String cover;

    @Size(max = 300, message = "뮤직 LRC은 최대 10000글자 까지 작성 가능합니다.")
    private final String lrc;
}
