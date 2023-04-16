package com.service.config.app;

import com.service.config.yaml.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "yaml")
@PropertySource(value = "classpath:application-util.yml", factory = YamlPropertySourceFactory.class)
@Data
public class AppConfig {
    @Value("${util-config.app_config.recent_popular_post_count}")
    private int recentAndPopular_post_count;

    @Value("${util-config.app_config.recent_comment_count}")
    private int recentCommentCount;

    @Value("${util-config.app_config.user_like_post_expire_days}")
    private long userLikePostExpireDays;

    @Value("${util-config.app_config.user_like_post_max_count}")
    private int userLikePostMaxCount;

    @Value("${util-config.app_config.max_post_content_size}")
    private int maxPostContentSize;
}