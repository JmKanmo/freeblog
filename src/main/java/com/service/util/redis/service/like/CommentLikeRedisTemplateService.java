package com.service.util.redis.service.like;

import com.service.util.redis.key.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeRedisTemplateService {
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
}
