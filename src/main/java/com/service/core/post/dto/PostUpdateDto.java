package com.service.core.post.dto;

import com.service.core.post.domain.Post;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class PostUpdateDto {
    private final Long id;
    private final String title;
    private final String contents;
    private final String thumbnailImage;
    private final List<String> tags;

    public static PostUpdateDto from(Post post) {
        return PostUpdateDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .thumbnailImage(post.getThumbnailImage())
                .tags(post.getTagList().stream().map(tag -> tag.getName()).collect(Collectors.toList()))
                .build();
    }
}
