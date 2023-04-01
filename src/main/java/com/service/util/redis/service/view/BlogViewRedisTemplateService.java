package com.service.util.redis.service.view;

import com.service.core.views.domain.BlogVisitors;
import com.service.util.redis.key.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 집계 방식: 로그인:(사용자 id + blogId), 비로그인: (ip, browser, etc...) 정보 조합
 */
@Service
@RequiredArgsConstructor
public class BlogViewRedisTemplateService {
    private final RedisTemplate redisTemplate;

    /**
     * @param blogId    (id + emailHash + blogId) => hashCode
     * @param visitorId 로그인(ip + user-agent(브라우저) + logged-in_ + email) hash | 비로그인(ip + user-agent(브라우저) + not-logged-in) => hashCode
     */
    public void visitBlog(int blogId, int visitorId) {
        BlogVisitors blogVisitors = getBlogVisitors(blogId);

        if (blogVisitors != null) {
            Set<Integer> visitorSet = blogVisitors.getVisitorSet();

            if (!visitorSet.contains(visitorId)) {
                visitorSet.add(visitorId);
                blogVisitors.incrementTodayVisitors();
            }
        } else {
            blogVisitors = BlogVisitors.builder()
                    .visitorSet(new HashSet<>())
                    .build();
            blogVisitors.getVisitorSet().add(visitorId);
            blogVisitors.incrementTodayVisitors();
        }
        blogVisitors.incrementTodayViews();
        getBlogVisitorsOperation().set(String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, blogId), blogVisitors);
    }

    public BlogVisitors getBlogVisitors(int blogId) {
        ValueOperations<String, BlogVisitors> blogVisitorsValueOperations = getBlogVisitorsOperation();
        return blogVisitorsValueOperations.get(String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, blogId));
    }

    private ValueOperations<String, BlogVisitors> getBlogVisitorsOperation() {
        return redisTemplate.opsForValue();
    }
}
