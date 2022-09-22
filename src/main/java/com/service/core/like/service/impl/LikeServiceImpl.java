package com.service.core.like.service.impl;

import com.service.core.like.service.LikeService;
import com.service.util.redis.service.RedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final RedisTemplateService redisTemplateService;
}
