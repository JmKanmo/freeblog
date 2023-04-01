package com.service.util;

import com.service.util.domain.SortType;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


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

    @Test
    public void randomTextTest() {
        String keyword = BlogUtil.createRandomAlphaNumberString(20);
        System.out.println(keyword);
    }

    @Test
    public void hashTest() {
        Long a = 5L;
        Long b = 25L;
        System.out.println(Objects.hash(a, b));

        Long c = 5l;
        Long d = 25L;
        System.out.println(Objects.hash(c, d));

        String strA = a.toString() + "&" + b.toString();

        String strC = c.toString() + "&" + d.toString();

        System.out.println(Objects.deepEquals(strA, strC));
    }

    @Test
    public void uuidTest() {
        String date = BlogUtil.formatLocalDateTimeToStrByPattern(BlogUtil.nowByZoneId(), "yyyy-MM-dd");
        String uuid = date + "/" + UUID.nameUUIDFromBytes("\"C:\\Users\\apdh1\\OneDrive\\사진\\스크린샷\\짤\\99b983892094b5c6d2fc3736e15da7d1.png\"".getBytes(StandardCharsets.UTF_8)) + "." + "png";
        System.out.println(uuid);
    }

    @Test
    public void numberCommaTest() {
        System.out.println(BlogUtil.formatNumberComma(1234234543));
    }

    @Test
    public void days() {
        System.out.println(Duration.ofDays(30).getSeconds());
        Duration duration = Duration.ofSeconds(2592000);
    }

    @Test
    public void sortTypeTest() {
        /**
         * postId
         * view + like
         * time
         */
        List<SortType<String, Long, Long>> sortTypes = Arrays.asList(
                SortType.<String, Long, Long>builder()
                        .v("jmkanmo")
                        .sort1(3L)
                        .sort2(42342232L)
                        .build(),
                SortType.<String, Long, Long>builder()
                        .v("nebiros")
                        .sort1(8L)
                        .sort2(324232231L)
                        .build(),
                SortType.<String, Long, Long>builder()
                        .v("yahoo")
                        .sort1(3L)
                        .sort2(32432422L)
                        .build(),
                SortType.<String, Long, Long>builder()
                        .v("혜옥이")
                        .sort1(1L)
                        .sort2(131121L)
                        .build(),
                SortType.<String, Long, Long>builder()
                        .v("정봉")
                        .sort1(15L)
                        .sort2(424232L)
                        .build(),
                SortType.<String, Long, Long>builder()
                        .v("혜옥이")
                        .sort1(9L)
                        .sort2(123142354L)
                        .build(),
                SortType.<String, Long, Long>builder()
                        .v("혜옥이")
                        .sort1(8L)
                        .sort2(1223142354L)
                        .build());

        Collections.sort(sortTypes, (o1, o2) -> {
            if (o1.getSort1() == o2.getSort1()) {
                return Long.compare(o2.getSort2(), o1.getSort2());
            } else {
                return Long.compare(o2.getSort1(), o1.getSort1());
            }
        });

        for (SortType<String, Long, Long> sortType : sortTypes) {
            System.out.println(sortType + "\n");
        }
    }
}