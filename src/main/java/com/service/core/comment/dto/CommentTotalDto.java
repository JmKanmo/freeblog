package com.service.core.comment.dto;

import com.service.core.user.dto.UserCommentDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class CommentTotalDto {
    private CommentParentDto commentParentDto;
    private List<CommentChildDto> commentChildDtoList;

    public static List<CommentTotalDto> from(List<CommentDto> commentDtoList, UserCommentDto userCommentDto, boolean isBlogOwner) {
        List<CommentTotalDto> commentTotalDtoList = new ArrayList<>();
        Map<Long, Integer> map = new HashMap<>();

        for (CommentDto commentDto : commentDtoList) {
            if (commentDto.getParentId() == 0) {
                CommentTotalDto commentTotalDto = new CommentTotalDto();
                CommentParentDto commentParentDto = CommentParentDto.from(commentDto);
                if (commentParentDto.isSecret()) {
                    if (isBlogOwner || (userCommentDto != null && userCommentDto.getUserId().equals(commentParentDto.getUserId())
                            && BCrypt.checkpw(userCommentDto.getUserPassword(), commentParentDto.getUserPassword()))) {
                        commentParentDto.setSecret(false);
                    }
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
                if (commentChildDto.isSecret()) {
                    if (isBlogOwner || (userCommentDto != null && userCommentDto.getUserId().equals(commentChildDto.getUserId())
                            && BCrypt.checkpw(userCommentDto.getUserPassword(), commentChildDto.getUserPassword()))) {
                        commentChildDto.setSecret(false);
                    }
                }
                commentChildDtoListRef.add(commentChildDto);
            }
        }
        return commentTotalDtoList;
    }
}
