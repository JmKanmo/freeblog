package com.service.util.redis.service.view;

import com.service.util.redis.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostViewRedisTemplateService {
    private final RedisTemplate redisTemplate;

    public Long incrementDayBlogViews(long blogId) {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();

        if (!hashOperations.hasKey(RedisTemplateKey.BLOG_DAY_VIEWS, blogId)) {
            hashOperations.put(RedisTemplateKey.BLOG_DAY_VIEWS, blogId, 0L);
        }

        long dayVisit = hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, blogId) + 1;
        hashOperations.put(RedisTemplateKey.BLOG_DAY_VIEWS, blogId, dayVisit);
        return dayVisit;
    }

    public void initDayBlogViews() {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();

        List<Long> keyList = new ArrayList<>();

        hashOperations.entries(RedisTemplateKey.BLOG_DAY_VIEWS).forEach((k, v) -> {
            keyList.add(k);
        });

        for (Long key : keyList) {
            hashOperations.put(RedisTemplateKey.BLOG_DAY_VIEWS, key, 0L);
        }
    }

    public Long incrementTotalBlogViews(long blogId) {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();

        if (!hashOperations.hasKey(RedisTemplateKey.BLOG_TOTAL_VIEWS, blogId)) {
            hashOperations.put(RedisTemplateKey.BLOG_TOTAL_VIEWS, blogId, 0L);
        }

        long totalVisit = hashOperations.get(RedisTemplateKey.BLOG_TOTAL_VIEWS, blogId) + 1;
        hashOperations.put(RedisTemplateKey.BLOG_TOTAL_VIEWS, blogId, totalVisit);
        return totalVisit;
    }

    public Long incrementPostViews(long postId) {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
        Long like = !hashOperations.hasKey(RedisTemplateKey.POST_VIEWS, postId) ? 1 : hashOperations.get(RedisTemplateKey.POST_VIEWS, postId) + 1;
        hashOperations.put(RedisTemplateKey.POST_VIEWS, postId, like);
        return like;
    }

    public Long getPostViews(long postId) {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
        Long like = !hashOperations.hasKey(RedisTemplateKey.POST_VIEWS, postId) ? 0 : hashOperations.get(RedisTemplateKey.POST_VIEWS, postId);
        return like;
    }
}
