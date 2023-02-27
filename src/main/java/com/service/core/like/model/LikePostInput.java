package com.service.core.like.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class LikePostInput {
    private Long postId;

    private Long blogId;

    @NotEmpty(message = "게시글 제목이 비어있습니다.")
    @NotBlank(message = "게시글 제목은 공백만 올 수 없습니다.")
    @Size(max = 128, message = "게시글 제목은 최대 128글자 입니다.")
    private String title;

    @NotEmpty(message = "사용자 썸네일 이미지가 비어있습니다.")
    @NotBlank(message = "사용자 썸네일 이미지는 공백이 올 수 없습니다.")
    @Size(max = 255, message = "사용자 썸네일 이미지는 최대 255글자 입니다.")
    private String userProfileThumbnailImage;
}
