package com.service.core.like.service;

import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.dto.UserLikePostDto;
import com.service.core.like.model.LikePostInput;

import java.security.Principal;

public interface LikeService {
    PostLikeDto getPostLikeDto(String id, Long postId);

    String postLike(Principal principal, LikePostInput likePostInput);

    UserLikePostDto getUserLikePostDto(Principal principal);
}
