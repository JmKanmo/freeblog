package com.service.core.views.service;

import com.service.core.views.domain.PostView;
import com.service.util.redis.service.view.PostViewRedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewService {
    private final PostViewRedisTemplateService postViewRedisTemplateService;

    public long viewPost(long blogId, long postId) throws Exception {
        PostView postView = postViewRedisTemplateService.viewPost(blogId, postId);
        return postView.getView();
    }

    public long getPostView(long blogId, long postId) throws Exception {
        PostView postView = postViewRedisTemplateService.getPostView(blogId, postId);
        return postView.getView();
    }

    public void deletePostView(long blogId, long postId) {
        postViewRedisTemplateService.deletePostView(blogId, postId);
    }

    public void deleteBlogPostView(long blogId) {
        postViewRedisTemplateService.deleteBlogPostView(blogId);
    }
}
