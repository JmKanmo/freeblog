package com.service.core.like.dto;

import com.service.core.like.domain.LikePost;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class PostLikeDto {
    private final boolean isLike;
    private final List<LikeUserDto> likeUserDtoList;

    @Data
    @Builder
    private static class LikeUserDto {
        private final Long postId;

        private final Long blogId;

        private final String id;

        private final String name;

        private final String thumbnailImage;

        public static LikeUserDto from(LikePost likePost) {
            return LikeUserDto.builder()
                    .postId(likePost.getPostId())
                    .blogId(likePost.getBlogId())
                    .id(likePost.getId())
                    .name(likePost.getName())
                    .thumbnailImage(likePost.getUserProfileThumbnailImage())
                    .build();
        }
    }

    public static PostLikeDto from(boolean isLike, List<LikePost> likePostList) {
        return PostLikeDto.builder()
                .isLike(isLike)
                .likeUserDtoList(likePostList.stream().map(LikeUserDto::from).collect(Collectors.toList()))
                .build();
    }
}
