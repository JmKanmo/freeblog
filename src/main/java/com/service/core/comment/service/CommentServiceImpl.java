package com.service.core.comment.service;

import com.service.core.comment.domain.Comment;
import com.service.core.comment.domain.CommentUser;
import com.service.core.comment.dto.CommentDto;
import com.service.core.comment.dto.CommentLinkDto;
import com.service.core.comment.dto.CommentRegisterResultDto;
import com.service.core.comment.dto.CommentSummaryDto;
import com.service.core.comment.model.CommentInput;
import com.service.core.comment.model.CommentUpdateInput;
import com.service.core.comment.paging.CommentPagination;
import com.service.core.comment.paging.CommentPaginationResponse;
import com.service.core.comment.paging.CommentSearchPagingDto;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.CommentManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostLinkDto;
import com.service.core.post.service.PostService;
import com.service.core.user.dto.UserCommentDto;
import com.service.core.user.service.UserService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import com.service.util.aws.s3.AwsS3Service;
import com.service.util.sftp.SftpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final AwsS3Service awsS3Service;
    private final SftpService sftpService;
    private final PostService postService;
    private final CommentInfoService commentInfoService;
    private final UserService userService;

    @Override
    public List<CommentLinkDto> findCommentLinkDto(Long blogId) {
        return commentInfoService.findCommentLinkDto(blogId);
    }

    @Override
    public String uploadAwsSCommentThumbnailImage(MultipartFile multipartFile) throws Exception {
        return awsS3Service.uploadImageFile(multipartFile);
    }

    @Override
    public String uploadSftpCommentThumbnailImage(MultipartFile multipartFile, String uploadKey) throws Exception {
        return sftpService.sftpImageFileUpload(multipartFile, ConstUtil.SFTP_COMMENT_IMAGE_HASH, uploadKey);
    }

    /**
     * 익명 일 때, 유효성 검사 진행 (닉네임 글자 수, 비어있거나, 올 공백, 비밀번호 유효성)
     * 비로그인 상태, 익명 게시글 작성 검사
     * 익명 상태에서 비밀글 작성 검사
     * <p>
     * 로그인 상태의 경우, 위에 유효성 검사 진행 X
     */
    @Transactional
    @Override
    public CommentRegisterResultDto registerComment(CommentInput commentInput, Principal principal) {
        if (BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous())) {
            if (!BlogUtil.checkFieldValidation(commentInput.getCommentUserNickname(), 255) || !BlogUtil.checkFieldValidation(commentInput.getCommentUserPassword(), 255)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
        }

        //  댓글 작성은 부모 댓글 포함 X, 잘못 된 요청의 경우에 유효성 검사 진행
        if (commentInput.getParentCommentId() != null) {
            if (!BlogUtil.checkFieldValidation(commentInput.getTargetUserId(), 255) || !BlogUtil.checkFieldValidation(commentInput.getTargetUserNickname(), 255)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
        }

        if ((principal == null || principal.getName() == null) && !BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous())) {
            throw new CommentManageException(ServiceExceptionMessage.NOT_LOGIN_ANONYMOUS_COMMENT);
        }

        if (BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous()) && BlogUtil.parseAndGetCheckBox(commentInput.getSecretComment())) {
            throw new CommentManageException(ServiceExceptionMessage.NOT_SECRET_WHEN_ANONYMOUS);
        }

        PostDto postDto = postService.findPostDtoById(commentInput.getCommentPostId());
        Comment comment = Comment.from(commentInput, Post.builder().id(postDto.getId()).build());

        if (!BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous()) && principal != null) {
            UserCommentDto userCommentDto = userService.findUserCommentDtoByEmail(principal.getName());
            CommentUser commentUser = comment.getCommentUser();
            commentUser.setUserProfileImage(userCommentDto.getUserProfileImage() == null || userCommentDto.getUserProfileImage().isEmpty() ? ConstUtil.UNDEFINED : userCommentDto.getUserProfileImage());
            commentUser.setUserNickname(userCommentDto.getUserNickname());
            commentUser.setUserId(userCommentDto.getUserId());
            commentUser.setUserPassword(BCrypt.hashpw(userCommentDto.getUserPassword(), BCrypt.gensalt()));
            commentUser.setOwner(postDto.getBlogId() == userCommentDto.getBlogId() ? true : false);
            comment.setCommentUser(commentUser);
        }
        Comment registeredComment = commentInfoService.saveComment(comment);
        return CommentRegisterResultDto.from(commentInfoService.findCommentCount(commentInput.getCommentPostId()), registeredComment.getId());
    }

    /**
     * 익명 일 때, 유효성 검사 진행 (닉네임 글자 수, 비어있거나, 올 공백, 비밀번호 유효성)
     * 부모 댓글 (로그인 여부에 상관 없이) 유효성 검사 진행 (닉네임 글자 수, 비어있거나, 올 공백, 비밀번호 유효성)
     * 비로그인 상태, 익명 게시글 작성 검사
     * 익명 상태에서 비밀글 작성 검사
     * <p>
     * 로그인 상태의 경우, 위에 유효성 검사 진행 X
     **/
    @Transactional
    @Override
    public void registerReplyComment(CommentInput commentInput, Principal principal) {
        if (BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous())) {
            if (!BlogUtil.checkFieldValidation(commentInput.getCommentUserNickname(), 255) || !BlogUtil.checkFieldValidation(commentInput.getCommentUserPassword(), 255)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
        }

        if (commentInput.getParentCommentId() != null) {
            if (!BlogUtil.checkFieldValidation(commentInput.getTargetUserId(), 255) || !BlogUtil.checkFieldValidation(commentInput.getTargetUserNickname(), 255)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }
        }

        if ((principal == null || principal.getName() == null) && !BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous())) {
            throw new CommentManageException(ServiceExceptionMessage.NOT_LOGIN_ANONYMOUS_COMMENT);
        }

        if (BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous()) && BlogUtil.parseAndGetCheckBox(commentInput.getSecretComment())) {
            throw new CommentManageException(ServiceExceptionMessage.NOT_SECRET_WHEN_ANONYMOUS);
        }

        PostDto postDto = postService.findPostDtoById(commentInput.getCommentPostId());
        Comment comment = Comment.from(commentInput, Post.builder().id(postDto.getId()).build());

        if (!BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous()) && principal != null) {
            UserCommentDto userCommentDto = userService.findUserCommentDtoByEmail(principal.getName());
            CommentUser commentUser = comment.getCommentUser();
            commentUser.setUserProfileImage(userCommentDto.getUserProfileImage());
            commentUser.setUserNickname(userCommentDto.getUserNickname());
            commentUser.setUserId(userCommentDto.getUserId());
            commentUser.setUserPassword(BCrypt.hashpw(userCommentDto.getUserPassword(), BCrypt.gensalt()));
            commentUser.setOwner(postDto.getBlogId() == userCommentDto.getBlogId() ? true : false);
            comment.setCommentUser(commentUser);
        }
        commentInfoService.saveComment(comment);
    }

    @Override
    public CommentPaginationResponse<CommentSummaryDto> findTotalPaginationComment(Long postId, Long ownerBlogId, CommentSearchPagingDto commentSearchPagingDto, Principal principal) {
        int commentCount = commentInfoService.findCommentCount(postId);
        int commentExistCount = commentInfoService.findCommentCountExist(postId);

        CommentPagination commentPagination = new CommentPagination(commentCount, commentSearchPagingDto);
        commentSearchPagingDto.setCommentPagination(commentPagination);
        String loginUserEmail = (principal == null || principal.getName() == null) ? null : principal.getName();
        boolean isBlogOwner = false;
        UserCommentDto userCommentDto = null;

        if (loginUserEmail != null) {
            userCommentDto = userService.findUserCommentDtoByEmail(loginUserEmail);
            Long loginUserBlogId = userCommentDto.getBlogId();

            if (loginUserBlogId == ownerBlogId) {
                isBlogOwner = true;
            }
        }
        List<CommentDto> commentDtoList = commentInfoService.findCommentDtoListByPaging(postId, commentSearchPagingDto);
        return new CommentPaginationResponse<>(CommentSummaryDto.from(commentDtoList, userCommentDto, commentExistCount, isBlogOwner), commentPagination);
    }

    @Override
    public CommentDto findCommentDtoById(Long commentId) {
        return CommentDto.from(commentInfoService.findCommentById(commentId));
    }

    /**
     * 익명 여부에 상관 없이 비밀번호 일치 여부 검사 진행
     * 익명 일 때, 유효성 검사 진행 (닉네임 글자 수, 비어있거나, 올 공백, 비밀번호 유효성)
     * 익명 일 때, 비밀글 작성 여부 검사 진행
     * 익명 아닐 때, 로그인 여부 검사 진행
     **/
    @Transactional
    @Override
    public void updateComment(CommentUpdateInput commentUpdateInput, Principal principal) {
        Comment comment = commentInfoService.findCommentById(commentUpdateInput.getCommentId());

        if (comment.isAnonymous()) {
            if (!BCrypt.checkpw(commentUpdateInput.getAuthCheckPassword(), comment.getCommentUser().getUserPassword())) {
                throw new CommentManageException(ServiceExceptionMessage.MISMATCH_COMMENT_PASSWORD);
            }

            if (!BlogUtil.checkFieldValidation(commentUpdateInput.getCommentUserNickname(), 255) || !BlogUtil.checkFieldValidation(commentUpdateInput.getCommentUserPassword(), 255)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_VALID_FORM_INPUT);
            }

            if (BlogUtil.parseAndGetCheckBox(commentUpdateInput.getSecretComment())) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_SECRET_WHEN_ANONYMOUS);
            }

            comment.setCommentImage(commentUpdateInput.getCommentThumbnailImage() == null || commentUpdateInput.getCommentThumbnailImage().isEmpty() ? ConstUtil.UNDEFINED : commentUpdateInput.getCommentThumbnailImage());
            comment.setSecret(BlogUtil.parseAndGetCheckBox(commentUpdateInput.getSecretComment()));
            comment.setComment(commentUpdateInput.getComment());
            CommentUser commentUser = comment.getCommentUser();
            commentUser.setUserNickname(commentUpdateInput.getCommentUserNickname());
            commentUser.setUserPassword(BCrypt.hashpw(commentUpdateInput.getCommentUserPassword(), BCrypt.gensalt()));
        } else {
            if ((principal == null || principal.getName() == null)) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_AUTHORITY_COMMENT);
            }
            UserCommentDto userCommentDto = userService.findUserCommentDtoByEmail(principal.getName());

            if (!BCrypt.checkpw(userCommentDto.getUserPassword(), comment.getCommentUser().getUserPassword())) {
                throw new CommentManageException(ServiceExceptionMessage.MISMATCH_COMMENT_PASSWORD);
            }
            comment.setCommentImage(commentUpdateInput.getCommentThumbnailImage() == null || commentUpdateInput.getCommentThumbnailImage().isEmpty() ? ConstUtil.UNDEFINED : commentUpdateInput.getCommentThumbnailImage());
            comment.setSecret(BlogUtil.parseAndGetCheckBox(commentUpdateInput.getSecretComment()));
            comment.setComment(commentUpdateInput.getComment());
        }
    }

    @Override
    public boolean checkAuthority(Long commentId, Principal principal) {
        CommentDto commentDto = findCommentDtoById(commentId);

        if (commentDto.isAnonymous()) {
            return true;
        } else {
            if ((principal == null || principal.getName() == null)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkExistComment(Long commentId) {
        return commentInfoService.findCommentById(commentId) != null;
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId, String password, Principal principal) {
        Comment comment = commentInfoService.findCommentById(commentId);

        if (comment.isAnonymous()) {
            if (!BCrypt.checkpw(password, comment.getCommentUser().getUserPassword())) {
                throw new CommentManageException(ServiceExceptionMessage.MISMATCH_COMMENT_PASSWORD);
            }
        } else {
            if (principal == null || principal.getName() == null) {
                throw new CommentManageException(ServiceExceptionMessage.NOT_AUTHORITY_COMMENT);
            }
            UserCommentDto userCommentDto = userService.findUserCommentDtoByEmail(principal.getName());

            if (!BCrypt.checkpw(userCommentDto.getUserPassword(), comment.getCommentUser().getUserPassword())) {
                throw new CommentManageException(ServiceExceptionMessage.MISMATCH_COMMENT_PASSWORD);
            }
        }

        int childCommentCount = commentInfoService.findChildCommentCount(commentId);

        if (childCommentCount <= 0) {
            if (comment.getParentId() > 0) {
                Comment parentComment = commentInfoService.findCommentUnlessDeleteById(comment.getParentId());
                if (parentComment.isDelete()) {
                    commentInfoService.deleteCommentById(parentComment.getId());
                }
            }
            commentInfoService.deleteCommentById(commentId);
        } else {
            comment.setDelete(true);
        }
    }

    @Override
    public List<PostLinkDto> findPostListDto(String userId) {
        return null;
    }
}
