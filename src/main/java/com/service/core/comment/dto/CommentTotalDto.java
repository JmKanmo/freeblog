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

    public static List<CommentTotalDto> from(List<CommentDto> commentDtoList, boolean isBlogOwner) {
        List<CommentTotalDto> commentTotalDtoList = new ArrayList<>();
        Map<Long, Integer> map = new HashMap<>();

        for (CommentDto commentDto : commentDtoList) {
            if (commentDto.getParentId() == 0) {
                CommentTotalDto commentTotalDto = new CommentTotalDto();
                CommentParentDto commentParentDto = CommentParentDto.from(commentDto);
                if (commentParentDto.isSecret() && isBlogOwner) {
                    commentParentDto.setSecret(false);
                }
                commentTotalDto.setCommentParentDto(commentParentDto);
                commentTotalDto.setCommentChildDtoList(new ArrayList<>());
                commentTotalDtoList.add(commentTotalDto);
                map.put(commentDto.getCommentId(), commentTotalDtoList.size() - 1);
            } else {
                Long parentId = commentDto.getParentId();
                int idx = map.get(parentId);
                List<CommentChildDto> commentChildDtoListRef = commentTotalDtoList.get(idx).getCommentChildDtoList();
                CommentChildDto commentChildDto = CommentChildDto.from(commentDto);
                if (commentChildDto.isSecret() && isBlogOwner) {
                    commentChildDto.setSecret(false);
                }
                commentChildDtoListRef.add(commentChildDto);
            }
        }
        return commentTotalDtoList;
    }
}
