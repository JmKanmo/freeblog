package com.service.core.like.service;

import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.dto.PostLikeResultDto;
import com.service.core.like.dto.UserLikePostDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.paging.LikePaginationResponse;
import com.service.core.like.paging.LikeSearchPagingDto;

import java.security.Principal;

public interface LikeService {
    LikePaginationResponse getPostLikeDto(Long postId, Long blogId, LikeSearchPagingDto likeSearchPagingDto) throws Exception;

    PostLikeResultDto getPostLikeResultDto(Principal principal, Long blogId, Long postId) throws Exception;

    boolean postLike(Principal principal, LikePostInput likePostInput) throws Exception;

    UserLikePostDto getUserLikePostDto(Principal principal) throws Exception;
}
