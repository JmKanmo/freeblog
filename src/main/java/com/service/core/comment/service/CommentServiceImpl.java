package com.service.core.comment.service;

import com.service.core.comment.domain.Comment;
import com.service.core.comment.domain.CommentUser;
import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.dto.CommentSearchDto;
import com.service.core.comment.dto.CommentSummaryDto;
import com.service.core.comment.model.CommentInput;
import com.service.core.comment.paging.CommentPagination;
import com.service.core.comment.paging.CommentPaginationResponse;
import com.service.core.comment.paging.CommentSearchPagingDto;
import com.service.core.comment.repository.mapper.CommentMapper;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.CommentManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.service.PostService;
import com.service.core.user.dto.UserCommentDto;
import com.service.core.user.service.UserService;
import com.service.util.BlogUtil;
import com.service.util.aws.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final AwsS3Service awsS3Service;
    private final PostService postService;
    private final CommentInfoService commentInfoService;
    private final UserService userService;

    private final CommentMapper commentMapper;

    @Override
    public String uploadAwsSCommentThumbnailImage(MultipartFile multipartFile) throws Exception {
        return awsS3Service.uploadImageFile(multipartFile);
    }

    @Override
    public void registerComment(CommentInput commentInput, Principal principal) {
        if (BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous())) {
            if (!BlogUtil.checkFieldValidation(commentInput.getCommentUserNickname(), 255) || !BlogUtil.checkFieldValidation(commentInput.getCommentUserPassword(), 255)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
        } else if (commentInput.getParentCommentId() != null) {
            if (!BlogUtil.checkFieldValidation(commentInput.getTargetUserId(), 255) || !BlogUtil.checkFieldValidation(commentInput.getTargetUserNickname(), 255)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
        } else if (principal == null && !BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous())) {
            throw new CommentManageException(ServiceExceptionMessage.NOT_LOGIN_ANONYMOUS_COMMENT);
        }

        Post post = postService.findPostById(commentInput.getCommentPostId());
        Comment comment = Comment.from(commentInput, post);

        if (!BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous()) && principal != null) {
            UserCommentDto userCommentDto = userService.findUserCommentDtoByEmail(principal.getName());
            CommentUser commentUser = comment.getCommentUser();
            commentUser.setUserNickname(userCommentDto.getUserNickname());
            commentUser.setUserId(userCommentDto.getUserId());
            commentUser.setUserPassword(BCrypt.hashpw(userCommentDto.getUserPassword(), BCrypt.gensalt()));
            commentUser.setOwner(post.getBlog().getId() == userCommentDto.getBlogId() ? true : false);
            comment.setCommentUser(commentUser);
        }
        commentInfoService.saveComment(comment);
    }

    @Override
    public CommentPaginationResponse<CommentSummaryDto> findTotalPaginationComment(Long postId, CommentSearchPagingDto commentSearchPagingDto) {
        int commentCount = commentMapper.findCommentCount(postId);
        CommentPagination commentPagination = new CommentPagination(commentCount, commentSearchPagingDto);
        commentSearchPagingDto.setCommentPagination(commentPagination);
        List<CommentDto> commentDtoList = commentMapper.findCommentDtoListByPaging(CommentSearchDto.from(postId, commentSearchPagingDto));
        return new CommentPaginationResponse<>(CommentSummaryDto.from(commentDtoList, commentCount), commentPagination);
    }
}
