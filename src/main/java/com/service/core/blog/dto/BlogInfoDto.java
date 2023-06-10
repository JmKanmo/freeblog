package com.service.core.blog.dto;

import com.service.core.blog.domain.Blog;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class BlogInfoDto {
    private final long id;
    private final String name;
    private final String intro;
    private final boolean isDelete;

    public static BlogInfoDto from(Blog blog) {
        if (blog == null) {
            return BlogInfoDto.builder()
                    .id(Long.MAX_VALUE)
                    .name(ConstUtil.UNDEFINED)
                    .intro(ConstUtil.UNDEFINED)
                    .isDelete(true)
                    .build();
        } else {
            return BlogInfoDto.builder()
                    .id(blog.getId())
                    .name(BlogUtil.ofNull(blog.getName()))
                    .intro(BlogUtil.ofNull(blog.getIntro()))
                    .isDelete(blog.isDelete())
                    .build();
        }
    }

    public static BlogInfoDto from(BlogMapperDto blogMapperDto) {
        return BlogInfoDto.builder()
                .id(blogMapperDto.getId())
                .name(BlogUtil.ofNull(blogMapperDto.getName()))
                .intro(BlogUtil.ofNull(blogMapperDto.getIntro()))
                .isDelete(blogMapperDto.getIsDelete())
                .build();
    }
}
