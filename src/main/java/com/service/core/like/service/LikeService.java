package com.service.core.like.service;

import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.dto.PostLikeResultDto;
import com.service.core.like.dto.UserLikePostDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.paging.LikePaginationResponse;
import com.service.core.like.paging.LikeSearchPagingDto;

import java.security.Principal;

public interface LikeService {
    LikePaginationResponse getPostLikeDto(Long postId, LikeSearchPagingDto likeSearchPagingDto);

    PostLikeResultDto getPostLikeResultDto(Principal principal, Long postId);

    boolean postLike(Principal principal, LikePostInput likePostInput);

    UserLikePostDto getUserLikePostDto(Principal principal);
}
