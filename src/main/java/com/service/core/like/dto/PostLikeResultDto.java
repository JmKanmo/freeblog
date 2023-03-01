package com.service.core.like.dto;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class PostLikeResultDto {
    private String message;
    private int responseCode;
    private boolean isLike;  // true: 좋아요 누름, false: 좋아요 취소
    private int likeCount;

    public static PostLikeResultDto success(boolean isLike) {
        return PostLikeResultDto.builder()
                .message("success")
                .responseCode(HttpStatus.OK.value())
                .isLike(isLike)
                .likeCount(0)
                .build();
    }

    public static PostLikeResultDto success(boolean isLike, int likeCount) {
        return PostLikeResultDto.builder()
                .message("success")
                .responseCode(HttpStatus.OK.value())
                .isLike(isLike)
                .likeCount(likeCount)
                .build();
    }

    public static PostLikeResultDto fail(Exception exception) {
        return PostLikeResultDto.builder()
                .message(String.format("fail: %s", BlogUtil.getErrorMessage(exception)))
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .isLike(false)
                .build();
    }
}
