package com.service.util.scheduler;

import com.service.util.redis.CacheKey;
import com.service.util.redis.RedisTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    private final RedisTemplateUtil redisTemplateUtil;

    @CacheEvict(value = CacheKey.FIND_USER_LIST_DTO, allEntries = true)
    @Scheduled(cron = "${scheduler.find-user-info}")
    public void deleteFindUserList() {
        try {
            // TODO
        } catch (Exception exception) {
            log.error("[freelog-deleteFindUserList] exception occurred! ", exception);
        }
    }

    @Scheduled(cron = "${scheduler.blog-day-views}")
    public void initBlogDayViews() {
        try {
            /**
             * TODO
             * 24시정각 Redis에 저장 된 모든 blog-day-views(일일 방문자) 데이터 0으로 초기화
             */
        } catch (Exception exception) {
            log.error("[freelog-initBlogDayViews] exception occurred! ", exception);
        }
    }
}
