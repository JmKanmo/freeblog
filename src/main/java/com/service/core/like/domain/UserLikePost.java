package com.service.core.like.domain;

import com.service.core.like.model.LikePostInput;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserLikePost implements Serializable {
    private static final long serialVersionUID = ConstUtil.SERIAL_VERSION_ID;

    private Long postId;

    private Long blogId;

    private String nickName;

    private String title;

    private String postThumbnailImage;

    public static UserLikePost from(LikePostInput likePostInput) {
        return UserLikePost.builder()
                .postId(likePostInput.getPostId())
                .blogId(likePostInput.getBlogId())
                .nickName(likePostInput.getNickName())
                .title(likePostInput.getTitle())
                .postThumbnailImage(likePostInput.getPostThumbnailImage())
                .build();
    }
}
