package com.service.core.like.domain;

import com.service.core.like.model.LikePostInput;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLikePost implements Serializable, Comparable<UserLikePost> {
    private static final long serialVersionUID = ConstUtil.SERIAL_VERSION_ID;

    private Long postId;

    private Long blogId;

    private String userId;

    private String nickName; // 게시글 작성자 닉네임

    private String title;

    private String postThumbnailImage;

    private long time;

    public static UserLikePost from(LikePostInput likePostInput) {
        return UserLikePost.builder()
                .postId(likePostInput.getPostId())
                .blogId(likePostInput.getBlogId())
                .userId(likePostInput.getId())
                .nickName(likePostInput.getWriter())
                .title(likePostInput.getTitle())
                .postThumbnailImage(likePostInput.getPostThumbnailImage())
                .time(System.currentTimeMillis())
                .build();
    }

    @Override
    public int compareTo(UserLikePost o) {
        return Long.compare(o.getTime(), time);
    }
}
