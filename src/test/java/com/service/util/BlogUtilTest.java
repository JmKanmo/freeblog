package com.service.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BlogUtilTest {
    @Test
    public void encryptEmailTest() {
        assertEquals(BlogUtil.encryptEmail("n@naver.com"),"n*****@naver.com");
        assertEquals(BlogUtil.encryptEmail("ne@naver.com"),"n*****@naver.com");
        assertEquals(BlogUtil.encryptEmail("dus@gmail.com"),"du*****@gmail.com");
        assertEquals(BlogUtil.encryptEmail("apdn1709@daum.net"),"ap*****@daum.net");
    }
}