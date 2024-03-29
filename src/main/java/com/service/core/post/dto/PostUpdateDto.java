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
    private final Long categoryId;
    private final String title;
    private final String contents;
    private final String thumbnailImage;
    private final List<String> tags;
    private final String metaKey;

    public static PostUpdateDto from(Post post) {
        return PostUpdateDto.builder()
                .id(post.getId())
                .categoryId(post.getCategory().getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .thumbnailImage(post.getThumbnailImage())
                .tags(post.getTagList().stream().map(tag -> tag.getName()).collect(Collectors.toList()))
                .metaKey(post.getMetaKey())
                .build();
    }
}
