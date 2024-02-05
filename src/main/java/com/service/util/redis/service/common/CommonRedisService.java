package com.service.util.redis.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommonRedisService {
    private final StringRedisTemplate stringRedisTemplate;

    public void setValueWithExpiration(String key, String value, long expirationMinutes) {
        // Set the value with expiration
        stringRedisTemplate.opsForValue().set(key, value, expirationMinutes, TimeUnit.MINUTES);
    }

    public String getValue(String key) {
        // Retrieve the value for the given key
        return stringRedisTemplate.opsForValue().get(key);
    }
}
