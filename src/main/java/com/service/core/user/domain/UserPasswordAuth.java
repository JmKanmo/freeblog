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
@RedisHash("user-password-auth")
public class UserPasswordAuth {
    @Id
    private Integer id;
    private String updatePasswordAuthKey;
    private LocalDateTime updatePasswordExpireDateTime;

    public static UserPasswordAuth from(Integer id) {
        return UserPasswordAuth.builder()
                .id(id)
                .updatePasswordAuthKey(BlogUtil.createRandomAlphaNumberString(20))
                .updatePasswordExpireDateTime(LocalDateTime.now())
                .build();
    }

    @TimeToLive
    public long getTimeToLive() {
        return Duration.ofDays(1).getSeconds();
    }
}
