package com.service.core.comment.repository.mapper;

import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.dto.CommentLinkDto;
import com.service.core.comment.dto.CommentSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<CommentDto> findCommentDtoListByPaging(CommentSearchDto commentSearchDto);

    List<CommentLinkDto> findCommentLinkDto(String userId);

    int findCommentCountExist(Long postId);

    int findCommentCount(Long postId);

    int findChildCommentCount(Long commentId);
}
