package com.service.util.redis.key;

public class CacheKey {
    public static final Long DEFAULT_EXPIRE_TTL_MINUTE = 60L * 24L * 30L * 1L; // 1 month
    public static final Long USER_HEADER_DTO_TTL_MINUTE = 60L * 24L * 30L * 1L; // 1 month
    public static final Long POST_DETAIL_DTO_TTL_MINUTE = 60L * 24L * 7L; // 1 weak

    public static final String USER_HEADER_DTO = "user-header-dto";
    public static final String POST_DETAIL_DTO = "post-detail-dto";
}
