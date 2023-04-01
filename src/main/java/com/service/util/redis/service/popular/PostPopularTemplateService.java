package com.service.util.redis.service.popular;

import com.service.config.app.AppConfig;
import com.service.core.post.dto.PostCardDto;
import com.service.util.redis.service.like.PostLikeRedisTemplateService;
import com.service.util.redis.service.view.PostViewRedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostPopularTemplateService {
    private final PostLikeRedisTemplateService postLikeRedisTemplateService;
    private final PostViewRedisTemplateService postViewRedisTemplateService;
    private final AppConfig appConfig;

    public List<PostCardDto> getPopularPost(long blogId) {

        return null;
    }
}
