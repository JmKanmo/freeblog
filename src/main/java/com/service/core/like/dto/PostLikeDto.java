package com.service.core.like.dto;

import com.service.core.like.domain.LikePost;
import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class PostLikeDto {
    private final boolean isLike;
    private final List<LikePost> likePostList;
    private final String message;
    private final int responseCode;

    public static PostLikeDto from(boolean isLike, List<LikePost> likePostList) {
        return PostLikeDto.builder()
                .isLike(isLike)
                .likePostList(likePostList)
                .message("success")
                .responseCode(HttpStatus.OK.value())
                .build();
    }

    public static PostLikeDto fail(Exception exception) {
        return PostLikeDto.builder()
                .isLike(false)
                .likePostList(Collections.emptyList())
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .build();
    }
}
