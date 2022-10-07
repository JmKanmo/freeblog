package com.service.core.comment.dto;

import com.service.core.user.dto.UserCommentDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommentSummaryDto {
    private final Integer totalCount;

    private final List<CommentTotalDto> commentTotalDtoList;

    public static CommentSummaryDto from(List<CommentDto> commentDtoList, UserCommentDto userCommentDto, int count, boolean isBlogOwner) {
        return CommentSummaryDto.builder()
                .totalCount(count)
                .commentTotalDtoList(CommentTotalDto.from(commentDtoList, userCommentDto, isBlogOwner))
                .build();
    }
}
