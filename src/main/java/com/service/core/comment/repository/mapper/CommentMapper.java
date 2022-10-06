package com.service.core.comment.repository.mapper;

import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.dto.CommentSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<CommentDto> findCommentDtoListByPaging(CommentSearchDto commentSearchDto);

    int findCommentCount(Long postId);
}
