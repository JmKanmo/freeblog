package com.service.core.like.domain;

import com.service.core.like.model.LikePostInput;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class LikePost implements Serializable {
    private static final long serialVersionUID = ConstUtil.SERIAL_VERSION_ID;

    private Long postId;

    private Long blogId;

    private String id;

    private String name; // 좋아요 누른 사용자 닉네임

    private String userProfileThumbnailImage;

    public static LikePost from(LikePostInput likePostInput) {
        return LikePost.builder()
                .postId(likePostInput.getPostId())
                .blogId(likePostInput.getBlogId())
                .id(likePostInput.getId())
                .name(likePostInput.getNickName())
                .userProfileThumbnailImage(likePostInput.getUserProfileThumbnailImage())
                .build();
    }
}
