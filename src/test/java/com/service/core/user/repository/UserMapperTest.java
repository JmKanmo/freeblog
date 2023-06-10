package com.service.core.user.repository;

import com.service.core.user.dto.UserProfileMapperDto;
import com.service.core.user.repository.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(readOnly = true)
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void mapperTest() {
        UserProfileMapperDto userProfileMapperDto = userMapper.findUserProfileMapperDtoByBlogId(4L);
        Assertions.assertNotNull(userProfileMapperDto);
    }
}
