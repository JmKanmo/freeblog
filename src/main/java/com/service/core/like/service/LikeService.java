package com.service.core.like.service;

import com.service.core.like.dto.PostLikeDto;

public interface LikeService {
    PostLikeDto getPostLikeDto(String id, Long postId);
}
