package com.service.util.redis;

public class RedisTemplateKey {
    public static final String COMMENT_LIKE = "comment-like";
    public static final String POST_LIKE = "post-like";

    //    public static final String POST_LIKE_USERS = "post-like-users";
    public static final String POST_VIEWS = "post-views";

    public static final String USER_LIKE_POST = "user-like-posts:%s:%d";

    public static final String BLOG_VIEWS = "blog-views:%s";
}
