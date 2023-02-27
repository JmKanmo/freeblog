package com.service.core.like.service.impl;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.LikeManageException;
import com.service.core.error.model.UserAuthException;
import com.service.core.like.domain.LikePost;
import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.dto.UserLikePostDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.service.LikeService;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.service.PostService;
import com.service.core.user.dto.UserHeaderDto;
import com.service.core.user.dto.UserProfileDto;
import com.service.core.user.service.UserService;
import com.service.util.redis.service.like.PostLikeRedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostLikeRedisTemplateService postLikeRedisTemplateService;

    private final PostService postService;

    private final UserService userService;

    @Override
    public PostLikeDto getPostLikeDto(String id, Long postId) {
        return postLikeRedisTemplateService.getPostLikeDto(id, postId);
    }


    public String postLike(Principal principal, LikePostInput likePostInput) {
        if (principal == null || principal.getName() == null) {
            throw new LikeManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
        }

        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
        PostDetailDto postDetailDto = postService.findPostDetailInfo(likePostInput.getBlogId(), likePostInput.getPostId());
        likePostInput.setLikePostInput(userHeaderDto, postDetailDto);

        return postLikeRedisTemplateService.doPostLike(likePostInput);
    }

    @Override
    public UserLikePostDto getUserLikePostDto(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new LikeManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
        }

        UserHeaderDto userHeaderDto = userService.findUserHeaderDtoByEmail(principal.getName());
        return UserLikePostDto.from(postLikeRedisTemplateService.getUserLikePostsById(userHeaderDto.getId()));
    }
}
