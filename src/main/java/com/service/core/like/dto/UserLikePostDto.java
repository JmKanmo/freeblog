package com.service.core.like.dto;

import com.service.core.like.domain.UserLikePost;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UserLikePostDto {
    private final List<UserLikePostInner> userLikePostInnerList;

    @Data
    @Builder
    private static class UserLikePostInner {
        private final Long postId;

        private final Long blogId;

        private final String nickName;

        private final String title;

        private final String postThumbnailImage;

        public static UserLikePostInner from(UserLikePost userLikePost) {
            return UserLikePostInner.builder()
                    .postId(userLikePost.getPostId())
                    .blogId(userLikePost.getBlogId())
                    .nickName(userLikePost.getNickName())
                    .title(userLikePost.getTitle())
                    .postThumbnailImage(userLikePost.getPostThumbnailImage())
                    .build();
        }
    }

    public static UserLikePostDto from(List<UserLikePost> userLikePosts) {
        return UserLikePostDto.builder()
                .userLikePostInnerList(userLikePosts.stream().map(UserLikePostInner::from).collect(Collectors.toList()))
                .build();
    }
}
