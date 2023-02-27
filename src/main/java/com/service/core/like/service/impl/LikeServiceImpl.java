package com.service.core.like.service.impl;

import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.service.LikeService;
import com.service.util.redis.service.like.PostLikeRedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostLikeRedisTemplateService postLikeRedisTemplateService;

    @Override
    public PostLikeDto getPostLikeDto(String id, Long postId) {
        return PostLikeDto.from(id == null ? false : true, null);
    }
}
