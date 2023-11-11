package com.service.config.audit;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
//    @Bean
//    public DateTimeProvider asiaDateTimeProvider() {
//        return () -> Optional.of(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
//    }
}
