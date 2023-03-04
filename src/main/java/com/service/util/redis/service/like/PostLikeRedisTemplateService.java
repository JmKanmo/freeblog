package com.service.util.redis.service.like;

import com.service.core.like.domain.LikePost;
import com.service.core.like.domain.UserLikePost;
import com.service.core.like.dto.PostLikeDto;
import com.service.core.like.dto.PostLikeResultDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.paging.LikeSearchPagingDto;
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

    public int getPostLikeCount(Long postId) {
        HashOperations<String, String, LikePost> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, postId);
        return hashOperations.values(postLikeKey).size();
    }

    public List<LikePost> getPostLikeDto(Long postId, LikeSearchPagingDto likeSearchPagingDto) {
        HashOperations<String, String, LikePost> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, postId);
        int beginIdx = likeSearchPagingDto.getLikePagination().getLimitStart() < 0 ? 0 : likeSearchPagingDto.getLikePagination().getLimitStart();
        int endIdx = beginIdx + likeSearchPagingDto.getRecordSize();

        if (endIdx > likeSearchPagingDto.getLikePagination().getTotalRecordCount()) {
            endIdx = likeSearchPagingDto.getLikePagination().getTotalRecordCount();
        }
        return hashOperations.values(postLikeKey).subList(beginIdx, endIdx);
    }

    public PostLikeResultDto getPostLikeResultDto(String id, Long postId) {
        HashOperations<String, String, LikePost> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, postId);
        Set<String> userIdSet = hashOperations.keys(postLikeKey);
        return PostLikeResultDto.success(userIdSet.contains(id), hashOperations.values(postLikeKey).size());
    }

    public boolean isPostLike(String id, Long postId) {
        HashOperations<String, String, LikePost> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, postId);
        Set<String> userIdSet = hashOperations.keys(postLikeKey);
        return userIdSet.contains(id);
    }

    public boolean doPostLike(LikePostInput likePostInput) {
        HashOperations<String, String, LikePost> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, likePostInput.getPostId());
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, likePostInput.getId());
        Set<String> userIdSet = hashOperations.keys(postLikeKey);

        if (userIdSet.contains(likePostInput.getId())) {
            // 좋아요 취소
            getLikePostOperation().delete(postLikeKey, likePostInput.getId());
            getUserLikePostOperation().rightPop(likePostKey);
            return false;
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
            return true;
        }
    }

    private ListOperations<String, UserLikePost> getUserLikePostOperation() {
        return redisTemplate.opsForList();
    }

    private HashOperations<String, String, LikePost> getLikePostOperation() {
        return redisTemplate.opsForHash();
    }
}
