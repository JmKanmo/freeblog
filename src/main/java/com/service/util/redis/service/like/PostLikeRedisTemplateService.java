package com.service.util.redis.service.like;

import com.service.config.app.AppConfig;
import com.service.core.like.domain.LikePost;
import com.service.core.like.domain.UserLikePost;
import com.service.core.like.dto.PostLikeResultDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.paging.LikeSearchPagingDto;
import com.service.util.redis.key.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostLikeRedisTemplateService {
    private final RedisTemplate redisTemplate;

    private final AppConfig appConfig;

    public List<Long> getPostLikeIdSet(long blogId) {
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
        HashOperations<String, Long, Map<String, LikePost>> likePostHashOperation = getLikePostOperation();
        List<Long> postIdSet = new ArrayList<>();

        for (Long postId : likePostHashOperation.keys(postLikeKey)) {
            Map<String, LikePost> likePostMap = likePostHashOperation.get(postLikeKey, postId);

            if (likePostMap.size() <= 0) {
                continue;
            }
            postIdSet.add(postId);
        }

        return postIdSet;
    }

    public List<UserLikePost> getUserLikePostsById(String id) {
        HashOperations<String, Long, UserLikePost> userLikePostOperation = getUserLikePostOperation();
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, id);
        List<UserLikePost> userLikePosts = userLikePostOperation.values(likePostKey);
        Collections.sort(userLikePosts);
        return userLikePosts;
    }

    public int getPostLikeCount(Long postId, Long blogId) {
        HashOperations<String, Long, Map<String, LikePost>> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
        Map<String, LikePost> likePostMap = hashOperations.get(postLikeKey, postId);
        return likePostMap == null ? 0 : likePostMap.size();
    }

    public List<LikePost> getPostLikeDto(Long postId, Long blogId, LikeSearchPagingDto likeSearchPagingDto) {
        HashOperations<String, Long, Map<String, LikePost>> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
        Map<String, LikePost> likePostMap = hashOperations.get(postLikeKey, postId);
        int beginIdx = likeSearchPagingDto.getLikePagination().getLimitStart() < 0 ? 0 : likeSearchPagingDto.getLikePagination().getLimitStart();
        int endIdx = beginIdx + likeSearchPagingDto.getRecordSize();

        if (endIdx > likeSearchPagingDto.getLikePagination().getTotalRecordCount()) {
            endIdx = likeSearchPagingDto.getLikePagination().getTotalRecordCount();
        }

        if (likePostMap == null) {
            return Collections.emptyList();
        } else {
            return (new ArrayList<>(likePostMap.values())).subList(beginIdx, endIdx);
        }
    }

    public PostLikeResultDto getPostLikeResultDto(String id, Long blogId, Long postId) {
        HashOperations<String, Long, Map<String, LikePost>> hashOperations = getLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
        Map<String, LikePost> likePostMap = hashOperations.get(postLikeKey, postId);

        if (likePostMap == null) {
            return PostLikeResultDto.success(false, 0);
        } else {
            return PostLikeResultDto.success(likePostMap.containsKey(id), likePostMap.size());
        }
    }

    public boolean doPostLike(LikePostInput likePostInput) {
        HashOperations<String, Long, Map<String, LikePost>> hashOperations = getLikePostOperation();
        HashOperations<String, Long, UserLikePost> userLikePostOperation = getUserLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, likePostInput.getBlogId());
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, likePostInput.getId());
        Map<String, LikePost> likePostMap = hashOperations.get(postLikeKey, likePostInput.getPostId()); // "userId":LikePost

        if (likePostMap == null) {
            // 해당 포스트에 사용자 정보 put
            likePostMap = new HashMap<>();
            likePostMap.put(likePostInput.getId(), LikePost.from(likePostInput));
            getLikePostOperation().put(postLikeKey, likePostInput.getPostId(), likePostMap);

            // 해당 사용자 좋아요 정보 push
            pushUserLike(userLikePostOperation, likePostKey, likePostInput);
            return true;
        } else {
            Set<String> userIdSet = likePostMap.keySet();

            if (userIdSet.contains(likePostInput.getId())) {
                // 좋아요 취소
                likePostMap.remove(likePostInput.getId());
                hashOperations.put(postLikeKey, likePostInput.getPostId(), likePostMap);
                userLikePostOperation.delete(likePostKey, likePostInput.getPostId());
                return false;
            } else {
                // 좋아요 누르기

                // 해당 포스트에 사용자 정보 put
                likePostMap.put(likePostInput.getId(), LikePost.from(likePostInput));
                getLikePostOperation().put(postLikeKey, likePostInput.getPostId(), likePostMap);

                // 해당 사용자 좋아요 정보 push
                pushUserLike(userLikePostOperation, likePostKey, likePostInput);
                return true;
            }
        }
    }

    private void pushUserLike(HashOperations<String, Long, UserLikePost> userLikePostOperation, String likePostKey, LikePostInput likePostInput) {
        redisTemplate.expire(likePostKey, Duration.ofSeconds(appConfig.getUserLikePostExpireDays()));
        UserLikePost userLikePost = UserLikePost.from(likePostInput);

        // 해당 사용자 좋아요 정보 push
        if (userLikePostOperation.size(likePostKey) < appConfig.getUserLikePostMaxCount()) {
            userLikePostOperation.put(likePostKey, likePostInput.getPostId(), userLikePost);
        } else {
            List<UserLikePost> userLikePosts = userLikePostOperation.values(likePostKey);
            Collections.sort(userLikePosts);
            long delIdx = userLikePosts.get(userLikePosts.size() - 1).getPostId();
            userLikePostOperation.delete(likePostKey, delIdx);
            userLikePostOperation.put(likePostKey, likePostInput.getPostId(), userLikePost);
        }
    }

    // 게시글 삭제 시에, 해당 블로거의 삭제 된 게시글 좋아요 기록 정보를 레디스에서 삭제
    public void deletePostLikeInfo(Long blogId, Long postId) {
        try {
            String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
            HashOperations<String, Long, Map<String, LikePost>> likePostHashOperation = getLikePostOperation();
            likePostHashOperation.delete(postLikeKey, postId);
        } catch (Exception e) {
            log.error("[PostLikeRedisTemplateService:deletePostLikeInfo] error =>", e);
        }
    }

    // 블로그 삭제 | 회원탈퇴 시에, 해당 블로거의 모든 게시글 좋아요 정보, 블로거의 좋아요 게시글 목록을 삭제
    public void deleteUserPostLikeInfo(Long blogId, String id) {
        try {
            // 해당 블로그의 모든 게시글 좋아요 정보 삭제
            String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
            HashOperations<String, Long, Map<String, LikePost>> likePostHashOperation = getLikePostOperation();
            likePostHashOperation.delete(postLikeKey, likePostHashOperation.keys(postLikeKey));

            // 해당 블로거가 좋아요 누른 모든 게시글 정보 삭제
            String likePostKey = String.format(RedisTemplateKey.LIKE_POST, id);
            HashOperations<String, Long, UserLikePost> userLikePostHashOperations = getUserLikePostOperation();
            userLikePostHashOperations.delete(likePostKey, userLikePostHashOperations.keys(likePostKey));
        } catch (Exception e) {
            log.error("[PostLikeRedisTemplateService:deleteUserPostLikeInfo] error =>", e);
        }
    }

    private HashOperations<String, Long, UserLikePost> getUserLikePostOperation() {
        return redisTemplate.opsForHash();
    }

    private HashOperations<String, Long, Map<String, LikePost>> getLikePostOperation() {
        return redisTemplate.opsForHash();
    }
}
