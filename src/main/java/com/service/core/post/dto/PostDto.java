package com.service.core.post.dto;

import com.service.core.post.domain.Post;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {
    private final Long id;
    private final String title;
    private final String thumbnailImage;
    private final String contents;
    private final String writer;
    private final String registerTime;
    private final String category;
    private final Long categoryId;

    public static PostDto fromEntity(Post post) {
        if (post == null) {
            return PostDto.builder()
                    .id(Long.MAX_VALUE)
                    .title(ConstUtil.UNDEFINED)
                    .thumbnailImage(ConstUtil.UNDEFINED)
                    .contents(ConstUtil.UNDEFINED)
                    .writer(ConstUtil.UNDEFINED)
                    .category(ConstUtil.UNDEFINED)
                    .categoryId(Long.MAX_VALUE)
                    .registerTime(BlogUtil.formatLocalDateTimeToStr(LocalDateTime.now()))
                    .build();
        } else {
            return PostDto.builder()
                    .id(post.getId())
                    .title(BlogUtil.ofNull(post.getTitle()))
                    .thumbnailImage(BlogUtil.ofNull(post.getThumbnailImage()))
                    .contents(BlogUtil.ofNull(post.getContents()))
                    .writer(BlogUtil.ofNull(post.getWriter()))
                    .category(BlogUtil.ofNull(post.getCategory().getName()))
                    .categoryId(post.getCategory().getId())
                    .registerTime(BlogUtil.formatLocalDateTimeToStr(post.getRegisterTime()))
                    .build();
        }
    }
}
