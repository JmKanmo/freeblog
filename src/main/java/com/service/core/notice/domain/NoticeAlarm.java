package com.service.core.notice.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.time.Duration;

@Data
@Builder
@RedisHash(value = "notice-alarm")
public class NoticeAlarm {
    @Id
    private Integer id;
    private String title;

    public static NoticeAlarm from(Integer id, String title) {
        return NoticeAlarm.builder()
                .id(id)
                .title(title)
                .build();
    }

    @TimeToLive
    public long timeToLive() {
        return Duration.ofDays(1).getSeconds();
    }
}
