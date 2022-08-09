package com.service.core.user.service;


import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.repository.UserRepository;
import com.service.core.user.repository.mapper.UserMapper;
import com.service.util.ConstUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
public class UserInfoServiceTest {
    @Autowired
    private UserInfoService userInfoService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @Test
    @DisplayName("특정 사용자 정보 저장")
    public void saveUserDomain() {
        when(userRepository.save(any())).thenReturn(UserDomain.builder().build());
        assertDoesNotThrow(() -> userInfoService.saveUserDomain(UserDomain.builder().build()));
        verify(userRepository, times(1)).save(any());
    }

    @ParameterizedTest
    @DisplayName("이메일을 통한 특정 사용자 포함여부 확인")
    @ValueSource(strings = "nebi25@naver.com")
    public void checkUserDomainByEmail(String email) {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertFalse(userInfoService.checkUserDomainByEmail(email));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(UserDomain.builder().build()));
        assertTrue(userInfoService.checkUserDomainByEmail(email));
    }

    @ParameterizedTest
    @DisplayName("id를 통한 특정 사용자 포함여부 확인")
    @ValueSource(strings = "nebi25")
    public void checkUserDomainById(String id) {
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertFalse(userInfoService.checkUserDomainById(id));

        when(userRepository.findById(id)).thenReturn(Optional.of(UserDomain.builder().build()));
        assertTrue(userInfoService.checkUserDomainById(id));
    }

    @ParameterizedTest
    @DisplayName("이메일을 통한 특정 사용자 정보 반환 없을 경우 예외반환")
    @ValueSource(strings = "nebi25@naver.com")
    public void findUserDomainByEmailOrThrow(String email) {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertEquals(ConstUtil.ExceptionMessage.ACCOUNT_INFO_NOT_FOUND.message(),
                assertThrows(UsernameNotFoundException.class,
                        () -> userInfoService.findUserDomainByEmailOrThrow(email)).getMessage());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(UserDomain.builder().build()));
        assertDoesNotThrow(() -> userInfoService.findUserDomainByEmailOrThrow(email));
    }

    @ParameterizedTest
    @DisplayName("이메일을 통한 특정 사용자 정보 반환 없을 경우 기본 값 반환")
    @ValueSource(strings = "nebi25@naver.com")
    public void findUserDomainByEmailOrElse(String email) {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertEquals(userInfoService.findUserDomainByEmailOrElse(email, null), null);

        UserDomain userDomain = UserDomain.builder().email("nebi25@naver.com").userId("nebi25").build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userDomain));
        assertEquals(userInfoService.findUserDomainByEmailOrElse(email, userDomain), userDomain);
    }

    @ParameterizedTest
    @DisplayName("id를 통한 특정 사용자 정보 반환 없을 경우 예외반환")
    @ValueSource(strings = "nebi25")
    public void findUserDomainByIdOrThrow(String id) {
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertEquals(ConstUtil.ExceptionMessage.ACCOUNT_INFO_NOT_FOUND.message(),
                assertThrows(UsernameNotFoundException.class,
                        () -> userInfoService.findUserDomainByIdOrThrow(id)).getMessage());

        when(userRepository.findById(id)).thenReturn(Optional.of(UserDomain.builder().build()));
        assertDoesNotThrow(() -> userInfoService.findUserDomainByIdOrThrow(id));
    }

    @ParameterizedTest
    @DisplayName("닉네임 통한 특정 사용자 정보 반환")
    @ValueSource(strings = "JmKanmo")
    public void findUsersByNickname(String nickname) {
        List<UserEmailFindDto> list = Arrays.asList(
                UserEmailFindDto.builder().build(),
                UserEmailFindDto.builder().build(),
                UserEmailFindDto.builder().build(),
                UserEmailFindDto.builder().build(),
                UserEmailFindDto.builder().build());

        when(userMapper.findUsersByNickName(nickname)).thenReturn(list);
        assertEquals(userInfoService.findUsersByNickName(nickname).size(), list.size());
    }
}
