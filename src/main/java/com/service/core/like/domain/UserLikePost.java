package com.service.core.like.domain;

import com.service.core.like.model.LikePostInput;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserLikePost implements Serializable {
    private static final long serialVersionUID = -6584044926029805156L;

    private Long postId;

    private Long blogId;

    private String userName;

    private String title;

    private String postThumbnailImage;

    public static UserLikePost from(LikePostInput likePostInput) {
        return UserLikePost.builder()
                .postId(likePostInput.getPostId())
                .blogId(likePostInput.getBlogId())
                .title(likePostInput.getTitle())
                .build();
    }
}
