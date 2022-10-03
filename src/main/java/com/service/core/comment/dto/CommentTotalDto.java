package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommentTotalDto {
    private final Integer totalCount;

    private final CommentParentDto commentParentDto;

    private final List<CommentChildDto> commentChildDtoList;
}
