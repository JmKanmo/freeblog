package com.service.core.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CommentTotalDto {
    private CommentParentDto commentParentDto;
    private List<CommentChildDto> commentChildDtoList;

    public static List<CommentTotalDto> from(List<CommentDto> commentDtoList) {
        List<CommentTotalDto> commentTotalDtoList = new ArrayList<>();
        Map<Long, Integer> map = new HashMap<>();

        for (CommentDto commentDto : commentDtoList) {
            if (commentDto.getParentId() == 0) {
                CommentTotalDto commentTotalDto = new CommentTotalDto();
                commentTotalDto.setCommentParentDto(CommentParentDto.from(commentDto));
                commentTotalDto.setCommentChildDtoList(new ArrayList<>());
                commentTotalDtoList.add(commentTotalDto);
                map.put(commentDto.getCommentId(), commentTotalDtoList.size() - 1);
            } else {
                Long parentId = commentDto.getParentId();
                int idx = map.get(parentId);
                List<CommentChildDto> commentChildDtoListRef = commentTotalDtoList.get(idx).getCommentChildDtoList();
                commentChildDtoListRef.add(CommentChildDto.from(commentDto));
            }
        }
        return commentTotalDtoList;
    }
}
