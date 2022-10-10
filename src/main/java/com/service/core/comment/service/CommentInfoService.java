package com.service.core.comment.service;

import com.service.core.comment.domain.Comment;
import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.paging.CommentSearchPagingDto;

import java.util.List;

public interface CommentInfoService {
    Comment saveComment(Comment comment);

    int findCommentCount(Long postId);

    int findCommentCountExist(Long postId);

    int findChildCommentCount(Long commentId);

    List<CommentDto> findCommentDtoListByPaging(Long postId, CommentSearchPagingDto commentSearchPagingDto);

    Comment findCommentById(Long commentId);

    Long deleteCommentById(Long commentId);
}
