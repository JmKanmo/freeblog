package com.service.config.redis;

import static org.junit.jupiter.api.Assertions.*;

import com.service.util.redis.RedisTemplateKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    void postLikeTest() {
        HashOperations<String, Long, Long> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(RedisTemplateKey.POST_LIKE, 1L, 25L);
        Long val = hashOperations.get(RedisTemplateKey.POST_LIKE, 1L);
        assertEquals(val, 25L);
        hashOperations.delete(RedisTemplateKey.POST_LIKE, 1L);
        val = hashOperations.get(RedisTemplateKey.POST_LIKE, 1L);
        System.out.println(val);
    }


//    @Test
//    void postViewTest() throws Exception {
//        ValueOperations<String, LikePost> valueOperations = redisTemplate.opsForValue();
//        String key = String.format(RedisTemplateKey.USER_LIKE_POST, "apdh1709@gmail.com", 1L);
//        valueOperations.set(key, new LikePost("안녕하세요", "반갑습니다."), 10, TimeUnit.SECONDS);
//        Thread.sleep(1500);
//        LikePost likePost = valueOperations.get(key);
//        System.out.println(likePost);
//
//        ScanOptions scanOptions = ScanOptions.scanOptions().match("*" + RedisTemplateKey.POST_VIEWS + ":apdh1709@gmail.com:*").count(10).build();
//        Cursor<LikePost> cursor = redisTemplate.scan(scanOptions);
//
//        while (cursor.hasNext()) {
//            String likePost1 = String.valueOf(cursor.next());
//            System.out.println(likePost1);
//        }
//    }

    @Test
    void deleteTest() {
        redisTemplate.delete(RedisTemplateKey.COMMENT_LIKE);
        redisTemplate.delete(RedisTemplateKey.POST_LIKE);
    }
}
