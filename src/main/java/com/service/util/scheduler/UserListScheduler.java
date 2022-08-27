package com.service.util.scheduler;

import com.service.util.redis.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserListScheduler {
    @CacheEvict(value = CacheKey.FIND_USER_LIST_DTO, allEntries = true)
    @Scheduled(cron = "${scheduler.find-user-info}")
    public void deleteFindUserList() {
        try {
            // TODO
        } catch (Exception exception) {
            log.error("[freelog-deleteFindUserList] exception occurred! ", exception);
        }
    }
}
