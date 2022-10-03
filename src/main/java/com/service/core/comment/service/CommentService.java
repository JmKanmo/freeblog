package com.service.core.comment.service;

import com.service.core.comment.model.CommentInput;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface CommentService {
    String uploadAwsSCommentThumbnailImage(MultipartFile multipartFile) throws Exception;

    void registerComment(CommentInput commentInput, Principal principal);
}
