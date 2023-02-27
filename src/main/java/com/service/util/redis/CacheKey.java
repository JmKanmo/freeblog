package com.service.util.redis;

public class CacheKey {
    public static final Long DEFAULT_EXPIRE_TTL_MINUTE = 60L * 24L * 30L * 12L; // 1 year
    public static final Long USER_HEADER_DTO_TTL_MINUTE = 60L * 24L * 30L * 3L; // 3 month
    public static final Long POST_DETAIL_DTO_TTL_MINUTE = 60L * 24L * 30L * 6L; // 6 month

    public static final String USER_HEADER_DTO = "user-header-dto";
    public static final String POST_DETAIL_DTO = "post-detail-dto";
}
