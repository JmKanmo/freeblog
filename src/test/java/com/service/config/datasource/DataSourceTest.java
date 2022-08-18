package com.service.config.datasource;

import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.repository.CustomUserRepository;
import com.service.core.user.service.UserInfoService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataSourceTest {
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private CustomUserRepository customUserRepository;

    @Test
    @Disabled
    void userInfoServiceTest() {
        UserDomain userDomain = userInfoService.findUserDomainByEmailOrThrow("apdh1709@gmail.com");
        System.out.println(userDomain.getEmail() +", " + userDomain.getNickname());
    }
}
