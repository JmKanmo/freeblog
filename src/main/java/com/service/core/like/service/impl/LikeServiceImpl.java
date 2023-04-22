package com.service.core.like.service.impl;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.LikeManageException;
import com.service.core.like.domain.LikePost;
import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.dto.PostLikeResultDto;
import com.service.core.like.dto.UserLikePostDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.paging.LikePagination;
import com.service.core.like.paging.LikePaginationResponse;
import com.service.core.like.paging.LikeSearchPagingDto;
import com.service.core.like.service.LikeService;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.service.PostService;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.service.UserService;
import com.service.util.redis.service.like.PostLikeRedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostLikeRedisTemplateService postLikeRedisTemplateService;

    private final PostService postService;

    private final UserService userService;

    @Override
    public LikePaginationResponse getPostLikeDto(Long postId, Long blogId, LikeSearchPagingDto likeSearchPagingDto) throws Exception {
        LikePagination likePagination = new LikePagination(postLikeRedisTemplateService.getPostLikeCount(postId, blogId), likeSearchPagingDto);
        likeSearchPagingDto.setLikePagination(likePagination);
        return new LikePaginationResponse<>(postLikeRedisTemplateService.getPostLikeDto(postId, blogId, likeSearchPagingDto), likeSearchPagingDto.getLikePagination());
    }

    @Override
    public PostLikeResultDto getPostLikeResultDto(Principal principal, Long blogId, Long postId) throws Exception {
        if (principal == null || principal.getName() == null) {
            return postLikeRedisTemplateService.getPostLikeResultDto(null, blogId, postId);
        } else {
            UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
            return postLikeRedisTemplateService.getPostLikeResultDto(userHeaderDto.getId(), blogId, postId);
        }
    }

    public boolean postLike(Principal principal, LikePostInput likePostInput) throws Exception {
        if (principal == null || principal.getName() == null) {
            throw new LikeManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
        }

        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
        PostDetailDto postDetailDto = postService.findPostDetailInfo(likePostInput.getBlogId(), likePostInput.getPostId());
        likePostInput.setLikePostInput(userHeaderDto, postDetailDto);

        return postLikeRedisTemplateService.doPostLike(likePostInput);
    }

    @Override
    public UserLikePostDto getUserLikePostDto(Principal principal) throws Exception {
        if (principal == null || principal.getName() == null) {
            throw new LikeManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
        }

        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
        return UserLikePostDto.from(postLikeRedisTemplateService.getUserLikePostsById(userHeaderDto.getId()));
    }

    @Override
    public void deleteUserLikedPost(String userId, Long postId) throws Exception {
        postLikeRedisTemplateService.deleteUserLikedPostInfo(userId, postId);
    }

    @Override
    public void deleteUserLikedPost(String userId) throws Exception {
        postLikeRedisTemplateService.deleteUserLikedPostInfo(userId);
    }
}
