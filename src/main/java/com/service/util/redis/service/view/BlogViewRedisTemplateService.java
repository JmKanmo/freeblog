package com.service.util.redis.service.view;

import com.service.core.views.domain.BlogVisitors;
import com.service.util.json.JsonUtil;
import com.service.util.redis.key.RedisTemplateKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * 집계 방식: 로그인:(사용자 id + blogId), 비로그인: (ip, browser, etc...) 정보 조합
 */
@Service
@Transactional
@RequiredArgsConstructor
public class BlogViewRedisTemplateService {
    private final RedisTemplate redisTemplate;

    private final JsonUtil jsonUtil;

    /**
     * @param blogId    (id + emailHash + blogId) => hashCode
     * @param visitorId 로그인(ip + user-agent(브라우저) + logged-in_ + email) hash | 비로그인(ip + user-agent(브라우저) + not-logged-in) => hashCode
     */
    public void visitBlog(int blogId, int visitorId) throws Exception {
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
        writeBlogVisitorsOperation(String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, blogId), blogVisitors);
    }

    public BlogVisitors getBlogVisitors(int blogId) throws Exception {
        ValueOperations<String, Object> blogVisitorsValueOperations = getBlogVisitorsOperation();
        return jsonUtil.readClzValue(String.valueOf(blogVisitorsValueOperations.get(String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, blogId))), BlogVisitors.class);
    }

    // 블로그 삭제 | 회원탈퇴 시에, 해당 블로그의 방문자 정보 삭제
    public void deleteBlogVisitors(int blogId) {
        ValueOperations<String, Object> blogVisitorsValueOperations = getBlogVisitorsOperation();
        blogVisitorsValueOperations.getAndDelete(String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, blogId));
    }

    private void writeBlogVisitorsOperation(String key, Object value) throws Exception {
        getBlogVisitorsOperation().set(key, jsonUtil.writeValueAsString(value));
    }

    private ValueOperations<String, Object> getBlogVisitorsOperation() {
        return redisTemplate.opsForValue();
    }
}
