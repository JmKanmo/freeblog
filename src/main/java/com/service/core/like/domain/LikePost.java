package com.service.core.like.domain;

import com.service.core.like.model.LikePostInput;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class LikePost implements Serializable {
    private static final long serialVersionUID = -6584044926029805156L;
    private Long postId;
    private String title;
    private String thumbnailImage;

    public static LikePost from(LikePostInput likePostInput) {
        return LikePost.builder()
                .postId(likePostInput.getPostId())
                .title(likePostInput.getTitle())
                .thumbnailImage(likePostInput.getThumbnailImage())
                .build();
    }
}
