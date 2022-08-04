package com.service.util;

import com.service.core.user.domain.UserEmailAuth;
import com.service.core.user.domain.UserPasswordAuth;
import com.service.core.user.repository.UserEmailAuthRepository;
import com.service.core.user.repository.UserPasswordAuthRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisTest {
    @Autowired
    private UserEmailAuthRepository userEmailAuthRepository;
    @Autowired
    private UserPasswordAuthRepository userPasswordAuthRepository;

    @Test
    public void ttlTest() throws InterruptedException {
        userEmailAuthRepository.save(UserEmailAuth.from(555));
        assertNotNull(userEmailAuthRepository.findById(555));

        userPasswordAuthRepository.save(UserPasswordAuth.from(1000));
        assertNotNull(userPasswordAuthRepository.findById(1000));
    }
}
