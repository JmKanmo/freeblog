package com.service.core.comment.service;

import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.dto.CommentLinkDto;
import com.service.core.comment.dto.CommentRegisterResultDto;
import com.service.core.comment.dto.CommentSummaryDto;
import com.service.core.comment.model.CommentInput;
import com.service.core.comment.model.CommentUpdateInput;
import com.service.core.comment.paging.CommentPaginationResponse;
import com.service.core.comment.paging.CommentSearchPagingDto;
import com.service.core.post.dto.PostLinkDto;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface CommentService {
    List<CommentLinkDto> findCommentLinkDto(Long blogId);

    String uploadAwsSCommentThumbnailImage(MultipartFile multipartFile) throws Exception;

    String uploadSftpCommentThumbnailImage(MultipartFile multipartFile, String uploadKey) throws Exception;

    CommentRegisterResultDto registerComment(CommentInput commentInput, Principal principal);

    void registerReplyComment(CommentInput commentInput, Principal principal);

    CommentPaginationResponse<CommentSummaryDto> findTotalPaginationComment(Long postId, Long ownerBlogId, CommentSearchPagingDto commentSearchPagingDto, Principal principal);

    CommentDto findCommentDtoById(Long commentId);

    void updateComment(CommentUpdateInput commentUpdateInput, Principal principal);

    boolean checkAuthority(Long commentId, Principal principal);

    boolean checkExistComment(Long commentId) throws Exception;

    void deleteComment(Long commentId, String password, Principal principal);

    List<PostLinkDto> findPostListDto(String userId);
}
