package com.service.util.scheduler;

import com.service.util.redis.CacheKey;
import com.service.util.redis.service.RedisTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    private final RedisTemplateService redisTemplateService;

    @Scheduled(cron = "${scheduler.blog-day-views}")
    public void initBlogDayViews() {
        try {
            redisTemplateService.initDayBlogViews();
        } catch (Exception exception) {
            log.error("[freeblog-initBlogDayViews] exception occurred! ", exception);
        }
    }
}
