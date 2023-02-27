package com.service.util.redis.service.like;

import com.service.core.like.domain.LikePost;
import com.service.core.like.model.LikePostInput;
import com.service.util.redis.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostLikeRedisTemplateService {
    private final RedisTemplate redisTemplate;
    public static final int LIKE_POST_MAX_COUNT = 30;

    public LikePost addLikePost(LikePostInput likePostInput, String email) {
        ListOperations<String, LikePost> likePostListOperations = redisTemplate.opsForList();
        String key = String.format(RedisTemplateKey.LIKE_POST, email);
        LikePost likePost = LikePost.from(likePostInput);

        if (likePostListOperations.size(key) >= LIKE_POST_MAX_COUNT) {
            likePostListOperations.rightPop(key);
        }

        likePostListOperations.leftPush(key, likePost);
        return likePost;
    }

    public List<LikePost> getLikePosts(String email) {
        ListOperations<String, LikePost> likePostListOperations = redisTemplate.opsForList();
        String key = String.format(RedisTemplateKey.LIKE_POST, email);
        return likePostListOperations.range(key, 0, likePostListOperations.size(key));
    }

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
