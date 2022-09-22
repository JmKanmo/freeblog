package com.service.util.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTemplateUtil {
    private final RedisTemplate redisTemplate;

    public Long incrementCommentLike(long commentId) {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
        Long like = !hashOperations.hasKey(RedisTemplateKey.COMMENT_LIKE, commentId) ? 1 : hashOperations.get(RedisTemplateKey.COMMENT_LIKE, commentId) + 1;
        hashOperations.put(RedisTemplateKey.COMMENT_LIKE, commentId, like);
        return like;
    }

    public Long getCommentLike(long commentId) {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
        Long like = !hashOperations.hasKey(RedisTemplateKey.COMMENT_LIKE, commentId) ? 0 : hashOperations.get(RedisTemplateKey.COMMENT_LIKE, commentId);
        return like;
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
