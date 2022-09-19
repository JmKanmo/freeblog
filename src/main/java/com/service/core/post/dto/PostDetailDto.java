package com.service.core.post.dto;

import com.service.core.category.domain.Category;
import com.service.core.post.domain.Post;
import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class PostDetailDto {
    private final String title;
    private final String contents;
    private final String categoryName;
    private final Long categoryId;
    private final Long blogId;
    private final String registerTime;
    private final String currentUrl;
    private final List<String> tags;

    // TODO 좋아요, 조회수, 댓글 추가

    public static PostDetailDto from(Post post) {
        return PostDetailDto.builder()
                .title(post.getTitle())
                .contents(post.getContents())
                .categoryName(post.getCategory().getName())
                .categoryId(post.getCategory().getId())
                .blogId(post.getBlog().getId())
                .registerTime(BlogUtil.formatLocalDateTimeToStr(post.getRegisterTime()))
                .currentUrl(BlogUtil.currentRequestUrl())
                .tags(post.getTagList().stream().map(tag -> tag.getName()).collect(Collectors.toList()))
                .build();
    }
}
