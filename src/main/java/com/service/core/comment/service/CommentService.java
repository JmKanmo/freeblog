package com.service.core.comment.service;

import com.service.core.comment.dto.CommentSummaryDto;
import com.service.core.comment.model.CommentInput;
import com.service.core.comment.paging.CommentPaginationResponse;
import com.service.core.comment.paging.CommentSearchPagingDto;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface CommentService {
    String uploadAwsSCommentThumbnailImage(MultipartFile multipartFile) throws Exception;

    void registerComment(CommentInput commentInput, Principal principal);

    CommentPaginationResponse<CommentSummaryDto> findTotalPaginationComment(Long postId, CommentSearchPagingDto commentSearchPagingDto);
}
