package com.service.core.comment.service;

import com.service.core.comment.domain.Comment;
import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.dto.CommentLinkDto;
import com.service.core.comment.dto.CommentSearchDto;
import com.service.core.comment.paging.CommentSearchPagingDto;
import com.service.core.comment.repository.CommentRepository;
import com.service.core.comment.repository.mapper.CommentMapper;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.CommentManageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInfoServiceImpl implements CommentInfoService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<CommentLinkDto> findCommentLinkDto(Long blogId) {
        return commentMapper.findCommentLinkDto(blogId);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public int findCommentCount(Long postId) {
        return commentMapper.findCommentCount(postId);
    }

    @Override
    public int findCommentCountExist(Long postId) {
        return commentMapper.findCommentCountExist(postId);
    }

    @Override
    public int findChildCommentCount(Long commentId) {
        return commentMapper.findChildCommentCount(commentId);
    }

    @Override
    public List<CommentDto> findCommentDtoListByPaging(Long postId, CommentSearchPagingDto commentSearchPagingDto) {
        return commentMapper.findCommentDtoListByPaging(CommentSearchDto.from(postId, commentSearchPagingDto));
    }

    @Override
    public Comment findCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentManageException(ServiceExceptionMessage.COMMENT_NOT_FOUND));

        if (comment.isDelete()) {
            throw new CommentManageException(ServiceExceptionMessage.ALREADY_DELETE_COMMENT);
        }
        return comment;
    }

    @Override
    public Comment findCommentUnlessDeleteById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentManageException(ServiceExceptionMessage.COMMENT_NOT_FOUND));
        return comment;
    }

    @Transactional
    @Override
    public Long deleteCommentById(Long commentId) {
        commentRepository.deleteById(commentId);
        return commentId;
    }
}
