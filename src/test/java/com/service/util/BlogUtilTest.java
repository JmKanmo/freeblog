package com.service.util;

import com.service.core.blog.domain.Blog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BlogUtilTest {
    @Test
    public void encryptEmailTest() {
        assertEquals(BlogUtil.encryptEmail("n@naver.com"), "n*****@naver.com");
        assertEquals(BlogUtil.encryptEmail("ne@naver.com"), "n*****@naver.com");
        assertEquals(BlogUtil.encryptEmail("dus@gmail.com"), "du*****@gmail.com");
        assertEquals(BlogUtil.encryptEmail("apdn1709@daum.net"), "ap*****@daum.net");
    }

    @Test
    public void formatLocalDateTimeTest() {
        String result = BlogUtil.formatLocalDateTimeToStrByPattern(LocalDateTime.now(), "yyyy.MM.dd HH:mm");
        System.out.println(result);
    }

    @Test
    public void formatLocalDateTimezoneTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul")); // 단순하게, 환경 상관없이 위 함수 사용  (댓글, 게시글 시간 저장 시에.... 타임존 설정)
        String ret = now != null ? now.format(formatter) : "";
        System.out.println(ret);
    }

    @Test
    public void createKeywordByTest() {
        String keyword = BlogUtil.createKeywordByText("hello world");
        System.out.println(keyword);
    }
}