package com.service.util.redis.service.view;


import com.service.core.views.domain.PostView;
import com.service.util.json.JsonUtil;
import com.service.util.redis.key.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostViewRedisTemplateService {
    private final RedisTemplate redisTemplate;

    private final JsonUtil jsonUtil;

    public long getPostViewCount(long postId, long blogId) throws Exception {
        String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
        PostView postView = jsonUtil.readClzValue(getPostViewHashOperation(postViewKey, postId), PostView.class);
        return postView == null ? 0 : postView.getView();
    }

    public List<Long> getPostViewIdSet(long blogId) {
        String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
        HashOperations<String, String, Object> postViewHashOperations = getPostViewHashOperation();
        List<Long> postIdSet = new ArrayList<>();

        for (String postId : postViewHashOperations.keys(postViewKey)) {
            try {
                PostView postView = jsonUtil.readClzValue(getPostViewHashOperation(postViewKey, postId), PostView.class);

                if (postView == null) {
                    continue;
                }

                postIdSet.add(Long.parseLong(postId));
            } catch (Exception e) {
                log.error("[PostViewRedisTemplateService:getPostViewIdSet] error:{}", e);
            }
        }

        return postIdSet;
    }

    public PostView viewPost(long blogId, long postId) throws Exception {
        String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
        HashOperations<String, String, Object> postViewHashOperations = getPostViewHashOperation();
        PostView postView = jsonUtil.readClzValue(getPostViewHashOperation(postViewKey, postId), PostView.class);

        if (postView == null) {
            postView = PostView.from(blogId, postId, 1);
        } else {
            postView.incrementView();
        }

        writePostViewHashOperation(postViewKey, String.valueOf(postId), postView);
        return postView;
    }

    public PostView getPostView(long blogId, long postId) throws Exception {
        String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
        PostView postView = jsonUtil.readClzValue(getPostViewHashOperation(postViewKey, postId), PostView.class);

        if (postView == null) {
            return PostView.from(blogId, postId, 1);
        } else {
            return postView;
        }
    }

    // 게시글 삭제 시에, 해당 게시글 조회수 정보를 삭제
    public void deletePostView(long blogId, long postId) {
        try {
            String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
            deletePostViewHashOperation(postViewKey, postId);
        } catch (Exception e) {
            log.error("[PostViewRedisTemplateService:deletePostView] error =>", e);
        }
    }

    // 블로그 삭제 | 회원탈퇴 시에, 해당 블로거의 모든 게시글 조회수 정보 삭제
    public void deleteBlogPostView(long blogId) {
        try {
            String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
            HashOperations<String, String, Object> postViewHashOperations = getPostViewHashOperation();
            postViewHashOperations.delete(postViewKey, postViewHashOperations.keys(postViewKey));
        } catch (Exception e) {
            log.error("[PostViewRedisTemplateService:deleteBlogPostView] error =>", e);
        }
    }

    private void writePostViewHashOperation(String key1, String key2, Object value2) throws Exception {
        getPostViewHashOperation().put(key1, key2, jsonUtil.writeValueAsString(value2));
    }

    private String getPostViewHashOperation(Object key, Object value) {
        return String.valueOf(getPostViewHashOperation().get(String.valueOf(key), String.valueOf(value)));
    }

    private void deletePostViewHashOperation(Object key, Object value) {
        getPostViewHashOperation().delete(String.valueOf(key), String.valueOf(value));
    }

    private HashOperations<String, String, Object> getPostViewHashOperation() {
        return redisTemplate.opsForHash();
    }
}
