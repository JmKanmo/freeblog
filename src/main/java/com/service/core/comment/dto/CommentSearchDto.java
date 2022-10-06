package com.service.core.comment.dto;

import com.service.core.comment.paging.CommentSearchPagingDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentSearchDto {
    private final Long postId;
    private final CommentSearchPagingDto commentSearchPagingDto;

    public static CommentSearchDto from(Long postId, CommentSearchPagingDto commentSearchPagingDto) {
        return CommentSearchDto.builder()
                .postId(postId)
                .commentSearchPagingDto(commentSearchPagingDto)
                .build();
    }
}
