package com.service;

import com.service.util.ConstUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class JmBlogApplication {
    @PostConstruct
    public void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ConstUtil.DEFAULT_SERVER_TIMEZONE));
    }

    public static void main(String[] args) {
        SpringApplication.run(JmBlogApplication.class, args);
    }
}
