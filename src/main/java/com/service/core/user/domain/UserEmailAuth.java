package com.service.core.user.domain;

import com.service.util.BlogUtil;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.time.Duration;
import java.time.LocalDateTime;

@Builder
@Data
@RedisHash(value = "user-auth")
public class UserEmailAuth {
    @Id
    private Integer id;
    private String emailAuthKey;

    public static UserEmailAuth from(Integer id) {
        return UserEmailAuth.builder()
                .id(id)
                .emailAuthKey(BlogUtil.createRandomAlphaNumberString(20))
                .build();
    }

    @TimeToLive
    public long timeToLive() {
        return Duration.ofDays(1).getSeconds();
    }
}
