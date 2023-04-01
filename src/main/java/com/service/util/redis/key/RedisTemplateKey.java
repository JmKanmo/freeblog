package com.service.util.redis.key;

import java.time.Duration;

public class RedisTemplateKey {
    // 댓글 관련 (좋아요)
    public static final String COMMENT_LIKE = "comment-like";

    // 게시글 관련 (좋아요)
    public static final String POST_LIKE = "post-like:%d";
    public static final String LIKE_POST = "like-posts:%s";

    // 게시글 관련 (조회수)
    public static final String POST_VIEWS = "post-views:%d";

    // 방문자 수 관련
    public static final String BLOG_VISITORS_COUNT = "blog-visitors-count:%d";
}
