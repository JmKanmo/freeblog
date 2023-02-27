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
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, id);
        return userLikePostOperation.range(likePostKey, 0, userLikePostOperation.size(likePostKey));
    }

    public PostLikeDto getPostLikeDto(String id, Long postId) {
        HashOperations<String, String, LikePost> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, postId);
        Set<String> userIdSet = hashOperations.keys(postLikeKey);
        return PostLikeDto.from(userIdSet.contains(id), hashOperations.values(postLikeKey));
    }

    public String doPostLike(LikePostInput likePostInput) {
        HashOperations<String, String, LikePost> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, likePostInput.getPostId());
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, likePostInput.getId());
        Set<String> userIdSet = hashOperations.keys(postLikeKey);

        if (userIdSet.contains(likePostInput.getId())) {
            // 좋아요 취소
            getLikePostOperation().entries(postLikeKey).remove(likePostInput.getId());
            return "좋아요를 취소했습니다.";
        } else {
            // 좋아요 누르기

            // 해당 포스트에 사용자 정보 put
            getLikePostOperation().put(postLikeKey, likePostInput.getId(), LikePost.from(likePostInput));

            // 해당 사용자 좋아요 정보 push
            ListOperations<String, UserLikePost> userLikePostOperation = getUserLikePostOperation();
            UserLikePost userLikePost = UserLikePost.from(likePostInput);

            if (userLikePostOperation.size(likePostKey) >= LIKE_POST_MAX_COUNT) {
                userLikePostOperation.rightPop(likePostKey);
            }

            userLikePostOperation.leftPush(likePostKey, userLikePost);
            return "좋아요를 눌렀습니다.";
        }
    }

    private ListOperations<String, UserLikePost> getUserLikePostOperation() {
        return redisTemplate.opsForList();
    }

    private HashOperations<String, String, LikePost> getLikePostOperation() {
        return redisTemplate.opsForHash();
    }
}
