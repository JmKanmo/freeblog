package com.service.core.post.model;

import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class BlogPostInput {
    @NotEmpty(message = "게시글 제목이 비어있습니다.")
    @NotBlank(message = "게시글 제목은 공백만 올 수 없습니다.")
    @Size(max = 128, message = "게시글 제목은 최대 128글자 까지 작성 가능합니다.")
    private final String title;

    @NotEmpty(message = "게시글 본문이 비어있습니다.")
    @NotBlank(message = "게시글 본문은 공백만 올 수 없습니다.")
    @Size(max = ConstUtil.MAX_POST_CONTENT_SIZE, message = "게시글 본문 크기가 허용 범위를 초과하였습니다.")
    private final String contents;

    /* 이미지 태그만 포함시키는 경우 고려 */
    @NotNull
//    @NotEmpty(message = "게시글 요약이 비어있습니다.")
    //    @NotBlank(message = "게시글 요약은 공백만 올 수 없습니다.")
    private final String summary;

    private final Long category;

    private final String tag;

    private final String postThumbnailImage;

    private final Long id;

    private final String metaKey;
}
