package com.service.core.comment.service;

import org.springframework.web.multipart.MultipartFile;

public interface CommentService {
    String uploadAwsSCommentThumbnailImage(MultipartFile multipartFile) throws Exception;
}
