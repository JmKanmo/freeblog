package com.service.util.redis.service.like;

import com.service.core.like.domain.LikePost;
import com.service.core.like.domain.UserLikePost;
import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.model.LikePostInput;
import com.service.util.redis.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostLikeRedisTemplateService {
    private final RedisTemplate redisTemplate;
    private static final int LIKE_POST_MAX_COUNT = 30;

    public List<UserLikePost> getUserLikePostsById(String id) {
        ListOperations<String, UserLikePost> userLikePostOperation = getUserLikePostOperation();
        String key = String.format(RedisTemplateKey.LIKE_POST, id);
        return userLikePostOperation.range(key, 0, userLikePostOperation.size(key));
    }

    public PostLikeDto getPostLikeDto(String id, Long postId) {
        HashOperations<String, String, LikePost> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, postId);
        Set<String> userIdSet = hashOperations.keys(postLikeKey);
        return PostLikeDto.from(userIdSet.contains(id), hashOperations.values(postLikeKey));
    }

    public void doPostLike(LikePostInput likePostInput) {
        // 해당 포스트에 사용자 정보 put
        getLikePostOperation().put(String.format(RedisTemplateKey.POST_LIKE, likePostInput.getPostId()), likePostInput.getId(), LikePost.from(likePostInput));

        // 해당 사용자 좋아요 정보 push
        ListOperations<String, UserLikePost> userLikePostOperation = getUserLikePostOperation();
        String key = String.format(RedisTemplateKey.LIKE_POST, likePostInput.getId());
        UserLikePost userLikePost = UserLikePost.from(likePostInput);

        if (userLikePostOperation.size(key) >= LIKE_POST_MAX_COUNT) {
            userLikePostOperation.rightPop(key);
        }

        userLikePostOperation.leftPush(key, userLikePost);
    }

    private ListOperations<String, UserLikePost> getUserLikePostOperation() {
        return redisTemplate.opsForList();
    }

    private HashOperations<String, String, LikePost> getLikePostOperation() {
        return redisTemplate.opsForHash();
    }
}
