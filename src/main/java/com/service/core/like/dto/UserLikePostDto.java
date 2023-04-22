package com.service.core.like.dto;

import com.service.core.like.domain.UserLikePost;
import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class UserLikePostDto {
    private final List<UserLikePostInner> userLikePostInnerList;
    private final String message;
    private final int responseCode;

    @Data
    @Builder
    private static class UserLikePostInner {
        private final Long postId;

        private final Long blogId;

        private final String userId;

        private final String nickName;

        private final String title;

        private final String postThumbnailImage;

        public static UserLikePostInner from(UserLikePost userLikePost) {
            return UserLikePostInner.builder()
                    .postId(userLikePost.getPostId())
                    .blogId(userLikePost.getBlogId())
                    .userId(userLikePost.getUserId())
                    .nickName(userLikePost.getNickName())
                    .title(userLikePost.getTitle())
                    .postThumbnailImage(userLikePost.getPostThumbnailImage())
                    .build();
        }
    }

    public static UserLikePostDto from(List<UserLikePost> userLikePosts) {
        return UserLikePostDto.builder()
                .responseCode(HttpStatus.OK.value())
                .message("success")
                .userLikePostInnerList(userLikePosts.stream().map(UserLikePostInner::from).collect(Collectors.toList()))
                .build();
    }

    public static UserLikePostDto fail(Exception exception) {
        return UserLikePostDto.builder()
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .build();
    }
}
