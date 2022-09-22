package com.service.config.redis;

import static org.junit.jupiter.api.Assertions.*;

import com.service.util.redis.RedisTemplateKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void incrementBlogViewTest() {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
        long dayVisit = hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 1L) == null ? 1 : hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 1L) + 1;
        hashOperations.put(RedisTemplateKey.BLOG_DAY_VIEWS, 1L, dayVisit);
        System.out.println(hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 1L));

        dayVisit = hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 2L) == null ? 1 : hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 2L) + 1;
        hashOperations.put(RedisTemplateKey.BLOG_DAY_VIEWS, 2L, dayVisit);
        System.out.println(hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 2L));

        dayVisit = hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 3L) == null ? 1 : hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 3L) + 1;
        hashOperations.put(RedisTemplateKey.BLOG_DAY_VIEWS, 3L, dayVisit);
        System.out.println(hashOperations.get(RedisTemplateKey.BLOG_DAY_VIEWS, 3L));
    }

    @Test
    void initBlotViewTest() {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();

        List<Long> keyList = new ArrayList<>();

        hashOperations.entries(RedisTemplateKey.BLOG_DAY_VIEWS).forEach((k, v) -> {
            keyList.add(k);
        });

        for (Long key : keyList) {
            hashOperations.put(RedisTemplateKey.BLOG_DAY_VIEWS, key, 0L);
        }
    }

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
