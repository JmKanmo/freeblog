package com.service.util;

import com.service.core.user.repository.UserEmailAuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisTest {
    @Autowired
    private UserEmailAuthRepository userEmailAuthRepository;

    @Test
    public void ttlTest() {
        
    }
}
