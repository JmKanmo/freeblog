package com.service.core.comment.service;

import com.service.core.comment.domain.Comment;
import com.service.core.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentInfoServiceImpl implements CommentInfoService {
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
