package com.service.config.redis;

import static org.junit.jupiter.api.Assertions.*;

import com.service.core.views.domain.BlogVisitors;
import com.service.util.redis.RedisTemplateKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void commentLikeEmptyTest() {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
//        Long like = hashOperations.get(RedisTemplateKey.COMMENT_LIKE, 1L) + 1;
        System.out.println(hashOperations.size(RedisTemplateKey.COMMENT_LIKE));
        System.out.println(hashOperations.get(RedisTemplateKey.COMMENT_LIKE, 1L));
        System.out.println(hashOperations.hasKey(RedisTemplateKey.COMMENT_LIKE, 1L));
    }

    @Test
    void commentLikeTest() {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(RedisTemplateKey.COMMENT_LIKE, 1L, 25L);
        Long val = hashOperations.get(RedisTemplateKey.COMMENT_LIKE, 1L);
        assertEquals(val, 25L);
        hashOperations.delete(RedisTemplateKey.COMMENT_LIKE, 1L);
        val = hashOperations.get(RedisTemplateKey.COMMENT_LIKE, 1L);
        System.out.println(val);
    }

    @Test
    void blogViewsInitTest() {
        ValueOperations<String, BlogVisitors> blogVisitorsValueOperations = redisTemplate.opsForValue();
        String blogVisitorId = String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, 2013161003); // 블로거 id

        blogVisitorsValueOperations.set(blogVisitorId,
                BlogVisitors.builder()
                        .visitorSet(new HashSet<>())
                        .build());

        int zerobaseId = "zerobase1".hashCode(); // 방문자 id
        BlogVisitors blogVisitors = blogVisitorsValueOperations.get(blogVisitorId);
        blogVisitors.getVisitorSet().add(zerobaseId);
        blogVisitors.incrementTodayViews();

        blogVisitorsValueOperations.set(blogVisitorId, blogVisitors);
    }

    @Test
    void blogViewsIncrementTest() {
        ValueOperations<String, BlogVisitors> blogVisitorsValueOperations = redisTemplate.opsForValue();
        String blogVisitorId = String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, 2013161003); // 방문 블로거 id
        int zerobaseId = "zerobase2".hashCode(); // 방문자 id
        BlogVisitors blogVisitors = blogVisitorsValueOperations.get(blogVisitorId);
        blogVisitors.getVisitorSet().add(zerobaseId);
        blogVisitors.incrementTodayViews();
        blogVisitorsValueOperations.set(blogVisitorId, blogVisitors);
    }

    @Test
    void blogViewsDeleteTest() {
        ValueOperations<String, BlogVisitors> blogVisitorsValueOperations = redisTemplate.opsForValue();
        String blogVisitorId = String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, 2013161003); // 방문 블로거 id
        BlogVisitors blogVisitors = blogVisitorsValueOperations.get(blogVisitorId);
        long todayView = blogVisitors.getTodayViews();
        blogVisitors.setTotalViews(blogVisitors.getTotalViews() + todayView);
        blogVisitors.setYesterdayViews(todayView);
        blogVisitors.setTodayViews(0);
        blogVisitors.getVisitorSet().clear();
        blogVisitorsValueOperations.set(blogVisitorId, blogVisitors);
    }

    @Test
    void blogViewsSearchTest() {
        ValueOperations<String, BlogVisitors> blogVisitorsValueOperations = redisTemplate.opsForValue();
        String blogVisitorId = String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, 2013161003);
        int zerobaseId = "zerobase1".hashCode();
        BlogVisitors blogVisitors = blogVisitorsValueOperations.get(blogVisitorId);
        Assertions.assertNotNull(blogVisitors);
        Set<Integer> blogVisitorSet = blogVisitors.getVisitorSet();
        Assertions.assertNotNull(blogVisitorSet);

        zerobaseId = "zerobase2".hashCode();
        blogVisitors = blogVisitorsValueOperations.get(blogVisitorId);
        Assertions.assertNotNull(blogVisitors);
        blogVisitorSet = blogVisitors.getVisitorSet();
        Assertions.assertNotNull(blogVisitorSet);
    }

    @Test
    void blogViewDeleteTest() {
        ValueOperations<String, BlogVisitors> blogVisitorsValueOperations = redisTemplate.opsForValue();
        String blogVisitorId = String.format(RedisTemplateKey.BLOG_VISITORS_COUNT, 2013161003); // 방문 블로거 i
        blogVisitorsValueOperations.getAndDelete(blogVisitorId);
    }

    @Test
    void postLikeTest() {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(RedisTemplateKey.POST_LIKE, 1L, 25L);
        Long val = hashOperations.get(RedisTemplateKey.POST_LIKE, 1L);
        assertEquals(val, 25L);
        hashOperations.delete(RedisTemplateKey.POST_LIKE, 1L);
        val = hashOperations.get(RedisTemplateKey.POST_LIKE, 1L);
        System.out.println(val);
    }


    @Test
    void rightInsertAndGetPostTest() throws Exception {
        ListOperations<String, LikePost> likePostListOperations = redisTemplate.opsForList();
        String key1 = String.format(RedisTemplateKey.LIKE_POST, "apdh1709@gmail.com");
        Long listSize = likePostListOperations.size(key1);

        likePostListOperations.rightPush(key1, new LikePost(1L, "반갑수다1", "apdh1709"));
        likePostListOperations.rightPush(key1, new LikePost(2L, "반갑수다2", "apdh1709"));
        likePostListOperations.rightPush(key1, new LikePost(3L, "반갑수다3", "apdh1709"));


        String key2 = String.format(RedisTemplateKey.LIKE_POST, "nebi25@naver.com");


        likePostListOperations.rightPush(key2, new LikePost(4L, "어솨요1", "nebi25"));
        likePostListOperations.rightPush(key2, new LikePost(5L, "어솨요2", "nebi25"));
        likePostListOperations.rightPush(key2, new LikePost(6L, "어솨요3", "nebi25"));


        likePostListOperations.rightPush(key2, new LikePost(7L, "어솨요4", "nebi25"));
        likePostListOperations.rightPush(key2, new LikePost(8L, "어솨요5", "nebi25"));
        likePostListOperations.rightPush(key2, new LikePost(9L, "어솨요6", "nebi25"));

        likePostListOperations.range(key1, 0, 3).forEach(likePost -> System.out.println(likePost.toString()));

        likePostListOperations.range(key2, 0, 6).forEach(likePost -> System.out.println(likePost.toString()));
    }

    @Test
    void leftInsertAndGetTest() {
        ListOperations<String, LikePost> likePostListOperations = redisTemplate.opsForList();
        String key = String.format(RedisTemplateKey.LIKE_POST, "apdh1709@gmail.com");

        likePostListOperations.leftPush(key, new LikePost(1L, "반갑수다1", "apdh1709"));
        likePostListOperations.leftPush(key, new LikePost(2L, "반갑수다2", "apdh1709"));
        likePostListOperations.leftPush(key, new LikePost(3L, "반갑수다3", "apdh1709"));

        likePostListOperations.range(key, 0, likePostListOperations.size(key)).forEach(likePost -> System.out.println(likePost.toString()));
        System.out.println("제거 후\n");
        likePostListOperations.rightPop(key);
        likePostListOperations.range(key, 0, likePostListOperations.size(key)).forEach(likePost -> System.out.println(likePost.toString()));
    }

    @Test
    void rightInsertAndGetTest() {
        ListOperations<String, LikePost> likePostListOperations = redisTemplate.opsForList();
        String key = String.format(RedisTemplateKey.LIKE_POST, "apdh1709@gmail.com");

        likePostListOperations.rightPush(key, new LikePost(1L, "반갑수다1", "apdh1709"));
        likePostListOperations.rightPush(key, new LikePost(2L, "반갑수다2", "apdh1709"));
        likePostListOperations.rightPush(key, new LikePost(3L, "반갑수다3", "apdh1709"));

        likePostListOperations.range(key, likePostListOperations.size(key), 0).forEach(likePost -> System.out.println(likePost.toString()));
        likePostListOperations.leftPop(key);

        System.out.println("제거 후\n");

        likePostListOperations.range(key, likePostListOperations.size(key), 0).forEach(likePost -> System.out.println(likePost.toString()));
    }

    @Test
    void deleteLikePostTest() {
        ListOperations<String, LikePost> likePostListOperations = redisTemplate.opsForList();
        String key2 = String.format(RedisTemplateKey.LIKE_POST, "nebi25@naver.com");

        likePostListOperations.range(key2, 0, likePostListOperations.size(key2)).forEach(likePost -> System.out.println(likePost.toString()));

        likePostListOperations.rightPop(key2, likePostListOperations.size(key2));

        System.out.println();

        likePostListOperations.range(key2, 0, likePostListOperations.size(key2)).forEach(likePost -> System.out.println(likePost.toString()));
    }

    @Test
    void deleteTest() {
        redisTemplate.delete(RedisTemplateKey.COMMENT_LIKE);
        redisTemplate.delete(RedisTemplateKey.POST_LIKE);
    }
}
