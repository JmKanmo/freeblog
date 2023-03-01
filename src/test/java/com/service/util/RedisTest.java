package com.service.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

@SpringBootTest
public class RedisTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void redisTest() {
        HashOperations<String, String, Integer> map = redisTemplate.opsForHash();
        map.put("jmkang", "nebiros1", 1);
        map.put("jmkang", "nebiros2", 2);
        map.put("jmkang", "nebiros3", 3);

        Assertions.assertEquals(map.size("jmkang"), 3);

        map.delete("jmkang", "nebiros1");
        map.delete("jmkang", "nebiros2");
        map.delete("jmkang", "nebiros3");

        Assertions.assertEquals(map.size("jmkang"), 0);
    }

    @Test
    public void deleteTest() {
        HashOperations<String, String, Integer> map = redisTemplate.opsForHash();
        map.delete("jmkang", "nebiros");
        System.out.println(map.size("jmkang"));
    }
}
