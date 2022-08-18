package com.service.core.user.repository;

import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.service.UserInfoService;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class CustomUserRepositoryTest {
    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void findUsersByNickName() {
        List<UserEmailFindDto> userEmailFindDtoList = userInfoService.findUsersByNickName("MATA");
        assertNotNull(userEmailFindDtoList);
        System.out.println(userEmailFindDtoList.size());
    }
}
