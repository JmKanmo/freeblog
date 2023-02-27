package com.service.core.like.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
public class LikePostInput {
    private String id;

    private String nickName;

    private String title;

    private String userProfileThumbnailImage;

    private String postThumbnailImage;

    private Long postId;

    private Long blogId;
}
