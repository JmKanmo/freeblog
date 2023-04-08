package com.service.util.redis.service.view;


import com.service.core.views.domain.PostView;
import com.service.util.redis.key.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostViewRedisTemplateService {
    private final RedisTemplate redisTemplate;

    public long getPostViewCount(long postId, long blogId) {
        String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
        HashOperations<String, Long, PostView> postViewHashOperations = getPostViewHashOperation();
        PostView postView = postViewHashOperations.get(postViewKey, postId);
        return postView == null ? 0 : postView.getView();
    }

    public List<Long> getPostViewIdSet(long blogId) {
        String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
        HashOperations<String, Long, PostView> postViewHashOperations = getPostViewHashOperation();
        List<Long> postIdSet = new ArrayList<>();

        for (Long postId : postViewHashOperations.keys(postViewKey)) {
            PostView postView = postViewHashOperations.get(postViewKey, postId);

            if (postView == null) {
                continue;
            }

            postIdSet.add(postId);
        }

        return postIdSet;
    }

    public PostView viewPost(long blogId, long postId) {
        String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
        HashOperations<String, Long, PostView> postViewHashOperations = getPostViewHashOperation();
        PostView postView = postViewHashOperations.get(postViewKey, postId);

        if (postView == null) {
            postView = PostView.from(blogId, postId, 1);
        } else {
            postView.incrementView();
        }

        postViewHashOperations.put(postViewKey, postId, postView);
        return postView;
    }

    public PostView getPostView(long blogId, long postId) {
        String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
        HashOperations<String, Long, PostView> postViewHashOperations = getPostViewHashOperation();
        PostView postView = postViewHashOperations.get(postViewKey, postId);

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
            HashOperations<String, Long, PostView> postViewHashOperations = getPostViewHashOperation();
            postViewHashOperations.delete(postViewKey, postId);
        } catch (Exception e) {
            log.error("[PostViewRedisTemplateService:deletePostView] error =>", e);
        }
    }

    // 블로그 삭제 | 회원탈퇴 시에, 해당 블로거의 모든 게시글 조회수 정보 삭제
    public void deleteBlogPostView(long blogId) {
        try {
            String postViewKey = String.format(RedisTemplateKey.POST_VIEWS, blogId);
            HashOperations<String, Long, PostView> postViewHashOperations = getPostViewHashOperation();
            postViewHashOperations.delete(postViewKey, postViewHashOperations.keys(postViewKey));
        } catch (Exception e) {
            log.error("[PostViewRedisTemplateService:deleteBlogPostView] error =>", e);
        }
    }

    private HashOperations<String, Long, PostView> getPostViewHashOperation() {
        return redisTemplate.opsForHash();
    }
}
