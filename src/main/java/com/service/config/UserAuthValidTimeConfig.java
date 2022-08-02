package com.service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class UserAuthValidTimeConfig {
    @Value("${util-config.user_auth.emailAuth-validTime}")
    private Integer emailAuthValidTime;

    @Value("${util-config.user_auth.updatePassword-validTime}")
    private Integer updatePasswordValidTime;
}
