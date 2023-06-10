package com.service.core.user.model;

import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Data
public class UserBasicInfoInput {
    @NotEmpty
    @NotBlank
    @Pattern(regexp = "^[a-z]{1}[a-z0-9]{4,11}$", message = "아이디 패턴에 어긋나는 형식입니다.")
    private final String id;

    @NotEmpty(message = "인사말이 비어있습니다.")
    @NotBlank(message = "인사말은 공백만 올 수 없습니다")
    @Size(max = 128, message = "인사말은 최대 128글자 까지 작성 가능합니다.")
    private final String greetings;

    @NotEmpty(message = "블로그명이 비어있습니다.")
    @NotBlank(message = "블로그명은 공백만 올 수 없습니다")
    @Size(max = 32, message = "블로그명은 최대 32글자 까지 작성 가능합니다.")
    private final String blogName;

    @NotEmpty(message = "닉네임이 비어있습니다.")
    @NotBlank(message = "닉네임은 공백만 올 수 없습니다")
    @Size(max = 20, message = "닉네임은 최대 20글자 까지 작성 가능합니다.")
    private final String nickname;

    @Size(max = ConstUtil.MAX_INTRO_CONTENT_SIZE, message = "게시글 컨텐츠 크기가 허용 범위를 초과하였습니다.")
    private final String intro;
}
