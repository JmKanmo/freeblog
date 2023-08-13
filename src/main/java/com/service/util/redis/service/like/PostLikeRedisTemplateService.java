package com.service.util.redis.service.like;

import com.service.config.app.AppConfig;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.LikeManageException;
import com.service.core.like.domain.LikePost;
import com.service.core.like.domain.UserLikePost;
import com.service.core.like.dto.PostLikeResultDto;
import com.service.core.like.model.LikePostInput;
import com.service.core.like.paging.LikeSearchPagingDto;
import com.service.util.json.JsonUtil;
import com.service.util.redis.key.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostLikeRedisTemplateService {
    private final RedisTemplate redisTemplate;
    private final JsonUtil jsonUtil;
    private final AppConfig appConfig;

    public List<Long> getPostLikeIdSet(long blogId) {
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
        HashOperations<String, String, Object> likePostHashOperation = getLikePostOperation();
        List<Long> postIdSet = new ArrayList<>();

        for (String postId : likePostHashOperation.keys(postLikeKey)) {
            try {
                Map<String, LikePost> likePostMap = jsonUtil.readMapValue(getLikePostOperation(postLikeKey, postId), String.class, LikePost.class);

                if (likePostMap.size() <= 0) {
                    continue;
                }
                postIdSet.add(Long.parseLong(postId));
            } catch (Exception e) {
                log.error("[PostLikeRedisTemplateService:getPostLikeIdSet] error:{}", e);
            }
        }
        return postIdSet;
    }

    public List<UserLikePost> getUserLikePostsById(String id) throws Exception {
        HashOperations<String, String, Object> userLikePostOperation = getUserLikePostOperation();
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, id);
        List<UserLikePost> userLikePosts = jsonUtil.readListValue(String.valueOf(userLikePostOperation.values(likePostKey)), UserLikePost.class);
        Collections.sort(userLikePosts);
        return userLikePosts;
    }

    public int getPostLikeCount(Long postId, Long blogId) throws Exception {
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
        Map<String, LikePost> likePostMap = jsonUtil.readMapValue(getLikePostOperation(postLikeKey, postId), String.class, LikePost.class);
        return likePostMap == null ? 0 : likePostMap.size();
    }

    public List<LikePost> getPostLikeDto(Long postId, Long blogId, LikeSearchPagingDto likeSearchPagingDto) throws Exception {
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
        Map<String, LikePost> likePostMap = jsonUtil.readMapValue(getLikePostOperation(postLikeKey, postId), String.class, LikePost.class);
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

    public PostLikeResultDto getPostLikeResultDto(String id, Long blogId, Long postId) throws Exception {
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
        Map<String, LikePost> likePostMap = jsonUtil.readMapValue(getLikePostOperation(postLikeKey, postId), String.class, LikePost.class);

        if (likePostMap == null) {
            return PostLikeResultDto.success(false, 0);
        } else {
            return PostLikeResultDto.success(likePostMap.containsKey(id), likePostMap.size());
        }
    }

    public boolean doPostLike(LikePostInput likePostInput) throws Exception {
        HashOperations<String, String, Object> userLikePostOperation = getUserLikePostOperation();
        String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, likePostInput.getBlogId());
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, likePostInput.getId());
        Map<String, LikePost> likePostMap = jsonUtil.readMapValue(getLikePostOperation(postLikeKey, likePostInput.getPostId()), String.class, LikePost.class); // "userId":LikePost

        if (likePostMap == null) {
            // 해당 포스트에 사용자 정보 put
            likePostMap = new HashMap<>();
            likePostMap.put(likePostInput.getId(), LikePost.from(likePostInput));
            writeLikePostHashOperation(postLikeKey, String.valueOf(likePostInput.getPostId()), likePostMap);

            // 해당 사용자 좋아요 정보 push
            pushUserLike(userLikePostOperation, likePostKey, likePostInput);
            return true;
        } else {
            Set<String> userIdSet = likePostMap.keySet();

            if (userIdSet.contains(likePostInput.getId())) {
                // 좋아요 취소
                likePostMap.remove(likePostInput.getId());
                writeLikePostHashOperation(postLikeKey, String.valueOf(likePostInput.getPostId()), likePostMap);
                deleteUserLikeHashOperation(likePostKey, likePostInput.getPostId());
                return false;
            } else {
                // 좋아요 누르기

                // 해당 포스트에 사용자 정보 put
                likePostMap.put(likePostInput.getId(), LikePost.from(likePostInput));
                writeLikePostHashOperation(postLikeKey, String.valueOf(likePostInput.getPostId()), likePostMap);

                // 해당 사용자 좋아요 정보 push
                pushUserLike(userLikePostOperation, likePostKey, likePostInput);
                return true;
            }
        }
    }

    private void pushUserLike(HashOperations<String, String, Object> userLikePostOperation, String likePostKey, LikePostInput likePostInput) throws Exception {
        redisTemplate.expire(likePostKey, Duration.ofSeconds(appConfig.getUserLikePostExpireDays()));
        UserLikePost userLikePost = UserLikePost.from(likePostInput);

        // 해당 사용자 좋아요 정보 push
        if (userLikePostOperation.size(likePostKey) < appConfig.getUserLikePostMaxCount()) {
            writeUserLikePostOperation(likePostKey, String.valueOf(likePostInput.getPostId()), userLikePost);
        } else {
            List<UserLikePost> userLikePosts = jsonUtil.readListValue(String.valueOf(userLikePostOperation.values(likePostKey)), UserLikePost.class);
            Collections.sort(userLikePosts);
            long delIdx = userLikePosts.get(userLikePosts.size() - 1).getPostId();
            deleteUserLikeHashOperation(likePostKey, delIdx);
            writeUserLikePostOperation(likePostKey, String.valueOf(likePostInput.getPostId()), userLikePost);
        }
    }

    // 게시글 삭제 시에, 해당 블로거의 삭제 된 게시글 좋아요 기록 정보를 레디스에서 삭제
    public void deletePostLikeInfo(Long blogId, Long postId) {
        try {
            String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
            deleteLikePostHashOperation(postLikeKey, postId);
        } catch (Exception e) {
            log.error("[PostLikeRedisTemplateService:deletePostLikeInfo] error =>", e);
        }
    }

    // 블로그 삭제 | 회원탈퇴 시에, 해당 블로거의 모든 게시글 좋아요 정보, 블로거의 좋아요 게시글 목록을 삭제
    public void deleteUserPostLikeInfo(Long blogId, String id) {
        try {
            // 해당 블로그의 모든 게시글 좋아요 정보 삭제
            String postLikeKey = String.format(RedisTemplateKey.POST_LIKE, blogId);
            HashOperations<String, String, Object> likePostHashOperation = getLikePostOperation();
            likePostHashOperation.delete(postLikeKey, likePostHashOperation.keys(postLikeKey));

            // 해당 블로거가 좋아요 누른 모든 게시글 정보 삭제
            String likePostKey = String.format(RedisTemplateKey.LIKE_POST, id);
            HashOperations<String, String, Object> userLikePostHashOperations = getUserLikePostOperation();
            userLikePostHashOperations.delete(likePostKey, userLikePostHashOperations.keys(likePostKey));
        } catch (Exception e) {
            log.error("[PostLikeRedisTemplateService:deleteUserPostLikeInfo] error =>", e);
        }
    }

    public void deleteUserLikedPostInfo(String userId, Long postId) {
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, userId);
        HashOperations<String, String, Object> userLikePostHashOperations = getUserLikePostOperation();

        if (getUserLikePostOperation(userId, postId).equals("null")) {
            throw new LikeManageException(ServiceExceptionMessage.LIKE_NOT_FOUND);
        }
        deleteUserLikeHashOperation(likePostKey, postId);
    }

    public void deleteUserLikedPostInfo(String userId) {
        String likePostKey = String.format(RedisTemplateKey.LIKE_POST, userId);
        HashOperations<String, String, Object> userLikePostHashOperations = getUserLikePostOperation();
        Set<String> keys = userLikePostHashOperations.keys(likePostKey);

        if (keys.size() <= 0) {
            throw new LikeManageException(ServiceExceptionMessage.LIKE_NOT_FOUND);
        }

        for (String key : keys) {
            deleteUserLikeHashOperation(likePostKey, key);
        }
    }

    private void writeLikePostHashOperation(String key1, String key2, Object obj) throws Exception {
        getLikePostOperation().put(key1, key2, jsonUtil.writeValueAsString(obj));
    }

    private void writeUserLikePostOperation(String key1, String key2, Object obj) throws Exception {
        getUserLikePostOperation().put(key1, key2, jsonUtil.writeValueAsString(obj));
    }

    private void deleteLikePostHashOperation(Object key, Object value) {
        getLikePostOperation().delete(String.valueOf(key), String.valueOf(value));
    }

    private void deleteUserLikeHashOperation(Object key, Object value) {
        getUserLikePostOperation().delete(String.valueOf(key), String.valueOf(value));
    }

    private String getLikePostOperation(Object key, Object value) {
        return String.valueOf(getLikePostOperation().get(String.valueOf(key), String.valueOf(value)));
    }

    private String getUserLikePostOperation(Object key, Object value) {
        return String.valueOf(getUserLikePostOperation().get(String.valueOf(key), String.valueOf(value)));
    }

    private HashOperations<String, String, Object> getUserLikePostOperation() {
        return redisTemplate.opsForHash();
    }

    private HashOperations<String, String, Object> getLikePostOperation() {
        return redisTemplate.opsForHash();
    }
}
