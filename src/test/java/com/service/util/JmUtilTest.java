package com.service.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JmUtilTest {
    @Test
    public void encryptEmailTest() {
        assertEquals(JmUtil.encryptEmail("n@naver.com"),"n*****@naver.com");
        assertEquals(JmUtil.encryptEmail("ne@naver.com"),"n*****@naver.com");
        assertEquals(JmUtil.encryptEmail("dus@gmail.com"),"du*****@gmail.com");
        assertEquals(JmUtil.encryptEmail("apdn1709@daum.net"),"ap*****@daum.net");
    }
}