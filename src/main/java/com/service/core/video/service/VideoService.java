package com.service.core.video.service;

import com.service.util.BlogUtil;
import com.service.util.redis.key.RedisTemplateKey;
import com.service.util.redis.service.common.CommonRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {
    private final CommonRedisService commonRedisService;

    /**
     * video token 생성, 최대 유효기간: 1minute
     */
    public String generateVideoToken(Principal principal) {
        String videoToken = String.format(RedisTemplateKey.VIDEO_TOKEN, BlogUtil.generateVideoToken(principal));
        commonRedisService.setValueWithExpiration(videoToken, videoToken, 1);
        return videoToken;
    }
}
