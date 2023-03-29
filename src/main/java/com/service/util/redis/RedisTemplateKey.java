package com.service.util.redis;

import java.time.Duration;

public class RedisTemplateKey {
    // 댓글 관련 (좋아요)
    public static final String COMMENT_LIKE = "comment-like";

    // 게시글 관련 (좋아요)
    public static final String POST_LIKE = "post-like:%d";
    public static final String LIKE_POST = "like-posts:%s";

    public static final Duration LIKE_POST_EXPIRE_DAYS = Duration.ofDays(30); // 최대 30일 동안 유효
    public static final long LIKE_POST_MAX_COUNT = 300; // 최대 300개 까지 좋아요 목록 저장

    // 게시글 관련 (조회수)
    public static final String POST_VIEWS = "post-views:%d";

    // 방문자 수 관련
    public static final String BLOG_VISITORS_COUNT = "blog-visitors-count:%d";
}
