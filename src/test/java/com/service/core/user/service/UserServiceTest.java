package com.service.core.user.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.email.service.EmailService;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.model.UserStatus;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private BlogService blogService;

    @MockBean
    private UserInfoService userInfoService;

    @MockBean
    private UserAuthService userAuthService;

    @Test
    @DisplayName("회원가입 정상 진행 테스트")
    public void processSignUpNormalTest() {
        when(userInfoService.checkUserDomainByEmail(any())).thenReturn(false);
        when(userInfoService.checkUserDomainById(any())).thenReturn(false);
        when(blogService.register(any())).thenReturn(Blog.builder().build());
        doNothing().when(userInfoService).saveUserDomain(any());
        when(userAuthService.saveUserEmailAuth(anyInt())).thenReturn("");
        when(userAuthService.findUserEmailAuthKey(anyInt())).thenReturn("");
        when(emailService.sendSignUpMail(anyString(), anyString())).thenReturn(0L);

        assertDoesNotThrow(() -> userService.processSignUp(UserSignUpInput.builder().email("nebi25@naver.com").id("nebi25").build(), UserDomain.builder().email("nebi25@naver.com").userId("nebi25").build()));
        verify(userAuthService, times(1)).saveUserEmailAuth(anyInt());
        verify(userAuthService, times(1)).findUserEmailAuthKey(anyInt());
        verify(emailService, times(1)).sendSignUpMail(anyString(), anyString());
    }

    @Test
    @DisplayName("회원가입 예외 발생 진행 테스트")
    public void processSignUpExceptionTest() {
        when(userInfoService.checkUserDomainByEmail(any())).thenReturn(true);
        when(userInfoService.checkUserDomainById(any())).thenReturn(true);
        assertThrowsExactly(UserManageException.class, () -> userService.processSignUp(UserSignUpInput.builder().email("nebi25@naver.com").id("nebi25").build(), UserDomain.builder().email("nebi25@naver.com").userId("nebi25").build()));
        verify(userAuthService, times(0)).saveUserEmailAuth(anyInt());
        verify(userAuthService, times(0)).findUserEmailAuthKey(anyInt());
        verify(emailService, times(0)).sendSignUpMail(anyString(), anyString());
    }

    @ParameterizedTest
    @DisplayName("특정 이메일의 사용자 활성상태 여부 체크 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void checkIsActive(String email) {
        // ACTIVE(인증 완료) 사용자의 경우
        when(userInfoService.findUserDomainByEmailOrThrow(email)).
                thenReturn(UserDomain.builder().status(UserStatus.ACTIVE).build());

        assertTrue(userService.checkIsActive(email));

        // 비인증 사용자의 경우
        when(userInfoService.findUserDomainByEmailOrThrow(email)).
                thenReturn(UserDomain.builder().status(UserStatus.NOT_AUTH).build());
        assertEquals(ConstUtil.ExceptionMessage.NOT_AUTHENTICATED_USER.message(), assertThrows(UserAuthException.class, () -> userService.checkIsActive(email)).getMessage());

        // 미등록 회원의 경우
        when(userInfoService.findUserDomainByEmailOrThrow(email)).
                thenThrow(new UsernameNotFoundException(ConstUtil.ExceptionMessage.USER_INFO_NOT_FOUND.message()));
        assertEquals(ConstUtil.ExceptionMessage.USER_INFO_NOT_FOUND.message(), assertThrows(UsernameNotFoundException.class, () -> userService.checkIsActive(email)).getMessage());
    }

    @Test
    @DisplayName("특정 이메일 or ID의 사용자가 존재하는지")
    void checkSameUser() {
        UserDomain userDomain = UserDomain.builder()
                .email("nebi25@naver.com")
                .userId("nebi25")
                .build();

        // 사용 가능 이메일
        when(userInfoService.checkUserDomainByEmail(userDomain.getEmail())).
                thenReturn(false);
        when(userInfoService.checkUserDomainById(userDomain.getUserId())).thenReturn(false);
        assertTrue(userService.checkSameUser(userDomain));

        // 이미 사용 중인 이메일
        when(userInfoService.checkUserDomainByEmail(userDomain.getEmail())).
                thenReturn(true);
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_SAME_EMAIL.message(), assertThrows(UserManageException.class, () -> userService.checkSameUser(userDomain)).getMessage());

        // 이미 사용 중인 ID
        when(userInfoService.checkUserDomainByEmail(userDomain.getEmail())).thenReturn(false);
        when(userInfoService.checkUserDomainById(userDomain.getUserId())).thenReturn(true);
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_SAME_ID.message(), assertThrows(UserManageException.class, () -> userService.checkSameUser(userDomain)).getMessage());
    }

    @ParameterizedTest
    @DisplayName("특정 ID의 사용자 존재 여부 테스트")
    @ValueSource(strings = "nebi25")
    void checkSameId(String id) {
        // 사용 가능 ID
        when(userInfoService.checkUserDomainById(id)).
                thenReturn(false);
        assertTrue(userService.checkSameId(id));

        // 이미 존재하는 ID
        when(userInfoService.checkUserDomainById(id)).thenReturn(true);
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_SAME_ID.message(), assertThrows(UserManageException.class, () -> userService.checkSameId(id)).getMessage());
    }

    @ParameterizedTest
    @DisplayName("특정 이메일의 사용자 존재 여부 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void checkSameEmail(String email) {
        // 사용 가능 이메일
        when(userInfoService.checkUserDomainByEmail(email)).
                thenReturn(false);
        assertTrue(userService.checkSameEmail(email));

        // 이미 존재하는 이메일
        when(userInfoService.checkUserDomainByEmail(email)).thenReturn(true);
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_SAME_EMAIL.message(), assertThrows(UserManageException.class, () -> userService.checkSameEmail(email)).getMessage());
    }

    @ParameterizedTest
    @DisplayName("이메일 인증 테스트")
    @CsvSource("FREELOG_KEY, nebi25@naver.com")
    void emailAuth(String key, String email) {
        // 정상 입력
        UserAuthInput normalUserAuthInput = UserAuthInput.builder().key(key).email(email).build();
        UserDomain normalUserDomain = UserDomain.builder()
                .email(normalUserAuthInput.getEmail())
                .isAuth(false).build();
        when(userInfoService.findUserDomainByEmailOrThrow(email)).thenReturn(normalUserDomain);
        when(userAuthService.checkUserEmailAuth(normalUserDomain, normalUserAuthInput)).thenReturn(true);
        doNothing().when(userInfoService).saveUserDomain(normalUserDomain);
        assertDoesNotThrow(() -> userService.emailAuth(normalUserAuthInput));
        verify(userAuthService, times(1)).checkUserEmailAuth(normalUserDomain, normalUserAuthInput);
        verify(userInfoService, times(1)).saveUserDomain(normalUserDomain);
    }

    @ParameterizedTest
    @DisplayName("이메일로 유저 검색 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void findUserByEmail(String email) {
        when(userInfoService.findUserDomainByEmailOrElse(email, null)).thenReturn(UserDomain.builder().email(email).build());
        assertNotNull(userService.findUserByEmail(email));
        verify(userInfoService, times(1)).findUserDomainByEmailOrElse(email, null);
    }

    @ParameterizedTest
    @DisplayName("닉네임에 해당하는 유저 체크 테스트")
    @CsvSource("nebi25, nebi25@naver.com")
    void findUsersByNickname(String nickname, String email) {
        List<UserEmailFindDto> userEmailFindDtoList = Arrays.asList(UserEmailFindDto.builder().nickname(nickname).email(email).build()
                , UserEmailFindDto.builder().nickname(nickname).email(email).build()
                , UserEmailFindDto.builder().nickname(nickname).email(email).build());
        // 존재하는 id
        when(userInfoService.findUsersByNickName(nickname)).thenReturn(userEmailFindDtoList);

        List<UserEmailFindDto> result = userService.findUsersByNickname(nickname);

        assertEquals(result.size(), userEmailFindDtoList.size());

        result.forEach(list -> {
            assertNotNull(list.getEmail());
            assertTrue(list.getEmail().equals(BlogUtil.encryptEmail(email)));
        });
    }

    @ParameterizedTest
    @DisplayName("이메일 변경 인증 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void updateEmailAuthCondition(String email) {
        UserDomain userDomain = UserDomain.builder().email(email).userId("nebi25").build();
        // 사용 가능 이메일
        when(userInfoService.findUserDomainByEmailOrThrow(email)).thenReturn(userDomain);
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        when(userAuthService.saveUserEmailAuth(anyInt())).thenReturn("test");
        assertTrue(userService.updateEmailAuthCondition(email).equals("test"));
    }

    @ParameterizedTest
    @DisplayName("비밀번호 변경 인증 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void updatePasswordAuthCondition(String email) {
        UserDomain userDomain = UserDomain.builder().email(email).build();
        // 사용 가능 이메일
        when(userInfoService.findUserDomainByEmailOrThrow(email)).thenReturn(userDomain);
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        when(userAuthService.saveUserPasswordAuth(anyInt())).thenReturn("test");
        assertTrue(userService.updatePasswordAuthCondition(email).equals("test"));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void updatePassword() {
        // 정상 입력
        UserPasswordInput userPasswordInput = UserPasswordInput.builder().email("nebi25@naver.com")
                .key("freelog-authentication-key").password("sdkels12!@").rePassword("sdkels12!@").build();

        UserDomain userDomain = UserDomain.builder()
                .email(userPasswordInput.getEmail())
                .build();

        when(userInfoService.findUserDomainByEmailOrThrow(userPasswordInput.getEmail())).thenReturn(userDomain);
        when(userAuthService.checkUserPasswordAuth(userDomain, userPasswordInput)).thenReturn(true);
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        assertDoesNotThrow(() -> userService.updatePassword(userPasswordInput));
        verify(userInfoService, times(1)).saveUserDomain(userDomain);
    }
}