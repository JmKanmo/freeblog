package com.service.util.redis.service.view;


import com.service.core.views.domain.PostView;
import com.service.util.redis.key.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewRedisTemplateService {
    private final RedisTemplate redisTemplate;

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

    private HashOperations<String, Long, PostView> getPostViewHashOperation() {
        return redisTemplate.opsForHash();
    }
}
