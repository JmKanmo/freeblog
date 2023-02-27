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
    private final List<LikePost> likePostList;

    public static PostLikeDto from(boolean isLike, List<LikePost> likePostList) {
        return PostLikeDto.builder()
                .isLike(isLike)
                .likePostList(likePostList)
                .build();
    }
}
