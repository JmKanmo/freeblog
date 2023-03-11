package com.service.core.post.dto;

import com.service.core.post.domain.Post;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostTitleDto {
    private final Long id;
    private final String title;
    private final String registerTime;
    private final Long blogId;

    public static PostDto fromEntity(Post post) {
        if (post == null) {
            return PostDto.builder()
                    .id(Long.MAX_VALUE)
                    .blogId(Long.MAX_VALUE)
                    .title(ConstUtil.UNDEFINED)
                    .registerTime(BlogUtil.formatLocalDateTimeToStrByPattern(LocalDateTime.now(), "yyyy.MM.dd HH:mm"))
                    .build();
        } else {
            return PostDto.builder()
                    .id(post.getId())
                    .blogId(post.getBlog().getId())
                    .title(BlogUtil.ofNull(post.getTitle()))
                    .registerTime(BlogUtil.formatLocalDateTimeToStrByPattern(post.getRegisterTime(), "yyyy.MM.dd HH:mm"))
                    .build();
        }
    }
}
