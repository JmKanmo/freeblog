package com.service.core.user.service;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.domain.UserEmailAuth;
import com.service.core.user.domain.UserPasswordAuth;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import com.service.core.user.repository.UserEmailAuthRepository;
import com.service.core.user.repository.UserPasswordAuthRepository;
import com.service.util.ConstUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
public class UserAuthServiceTest {
    @Autowired
    private UserAuthService userAuthService;

    @MockBean
    private UserEmailAuthRepository userEmailAuthRepository;

    @MockBean
    private UserPasswordAuthRepository userPasswordAuthRepository;

    @ParameterizedTest
    @DisplayName("특정 사용자의 이메일 인증키 발급")
    @ValueSource(ints = 255)
    public void saveUserEmailAuth(int id) {
        // 기존 사용자의 인증키 발급
        UserEmailAuth normalUserEmailAuth = UserEmailAuth.builder().build();
        when(userEmailAuthRepository.existsById(id)).thenReturn(true);
        when(userEmailAuthRepository.findById(id)).thenReturn(Optional.of(normalUserEmailAuth));
        when(userEmailAuthRepository.save(normalUserEmailAuth)).thenReturn(normalUserEmailAuth);
        assertTrue(userAuthService.saveUserEmailAuth(id).length() == 20);
        verify(userEmailAuthRepository, times(1)).save(normalUserEmailAuth);

        // 신규 사용자의 인증키 발급
        UserEmailAuth failUserEmailAuth = UserEmailAuth.builder().emailAuthKey("nebiros").build();
        when(userEmailAuthRepository.existsById(id)).thenReturn(false);
        when(userEmailAuthRepository.save(any())).thenReturn(failUserEmailAuth);
        String resultKey = userAuthService.saveUserEmailAuth(id);
        assertTrue(resultKey.equals("nebiros"));
        verify(userEmailAuthRepository, atLeast(1)).save(any());
    }

    @ParameterizedTest
    @DisplayName("특정 사용자의 비밀번호 인증키 발급")
    @ValueSource(ints = 255)
    public void saveUserPasswordAuth(int id) {
        // 기존 사용자의 인증키 발급
        UserPasswordAuth normalUserPasswordAuth = UserPasswordAuth.builder().build();
        when(userPasswordAuthRepository.existsById(id)).thenReturn(true);
        when(userPasswordAuthRepository.findById(id)).thenReturn(Optional.of(normalUserPasswordAuth));
        when(userPasswordAuthRepository.save(normalUserPasswordAuth)).thenReturn(normalUserPasswordAuth);
        assertTrue(userAuthService.saveUserPasswordAuth(id).length() == 20);
        verify(userPasswordAuthRepository, times(1)).save(normalUserPasswordAuth);

        // 신규 사용자의 인증키 발급
        UserPasswordAuth failUserPasswordAuth = UserPasswordAuth.builder().updatePasswordAuthKey("nebiros").build();
        when(userPasswordAuthRepository.existsById(id)).thenReturn(false);
        when(userPasswordAuthRepository.save(any())).thenReturn(failUserPasswordAuth);
        String resultKey = userAuthService.saveUserPasswordAuth(id);
        assertTrue(resultKey.equals("nebiros"));
        verify(userPasswordAuthRepository, atLeast(1)).save(any());
    }

    @ParameterizedTest
    @DisplayName("특정 사용자의 이메일 인증키 체크")
    @ValueSource(ints = 255)
    public void findUserEmailAuthKey(int id) {
        // 인증키가 없거나 만료된 경우
        when(userEmailAuthRepository.findById(id)).thenReturn(Optional.empty());
        assertEquals(ServiceExceptionMessage.AUTH_KEY_NOT_FOUND.message(), assertThrows(UserAuthException.class, () -> userAuthService.findUserEmailAuthKey(id)).getMessage());

        // 인증키가 있는 경우
        when(userEmailAuthRepository.findById(id)).thenReturn(Optional.of(UserEmailAuth.builder().emailAuthKey("nebiros").build()));
        assertTrue(userAuthService.findUserEmailAuthKey(id).equals("nebiros"));
    }

    @Test
    @DisplayName("사용자 이메일 인증 입력 폼 체크")
    public void checkUserEmailAuth() {
        // 인증키가 없거나 만료된 경우
        when(userEmailAuthRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertEquals(ServiceExceptionMessage.AUTH_KEY_NOT_FOUND.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserEmailAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").build(),
                                UserAuthInput.builder().email("nebi25@naver.com").key("nebiros").build())).getMessage());

        // 인증키 정보가 올바르지 않은 경우 (널값)
        when(userEmailAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserEmailAuth.builder().emailAuthKey(null).build()));
        assertEquals(ServiceExceptionMessage.AUTH_VALID_KEY_MISMATCH.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserEmailAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").build(),
                                UserAuthInput.builder().email("nebi25@naver.com").key("nebiros").build())).getMessage());

        // 인증키 정보가 올바르지 않은 경우 (사용자 입력값, 발급 인증키 정보 불일치)
        when(userEmailAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserEmailAuth.builder().emailAuthKey("dark night").build()));
        assertEquals(ServiceExceptionMessage.AUTH_VALID_KEY_MISMATCH.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserEmailAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").build(),
                                UserAuthInput.builder().email("nebi25@naver.com").key("nebiros").build())).getMessage());

        // 이미 인증 된 사용자
        when(userEmailAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserEmailAuth.builder().emailAuthKey("nebiros").build()));
        assertEquals(ServiceExceptionMessage.ALREADY_AUTHENTICATED_ACCOUNT.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserEmailAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").isAuth(true).build(),
                                UserAuthInput.builder().email("nebi25@naver.com").key("nebiros").build())).getMessage());

        // 정상적으로 인증
        when(userEmailAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserEmailAuth.builder().emailAuthKey("nebiros").build()));
        when(userEmailAuthRepository.save(any())).thenReturn(UserEmailAuth.builder().build());
        assertDoesNotThrow(() -> userAuthService.checkUserEmailAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").isAuth(false).build(),
                UserAuthInput.builder().email("nebi25@naver.com").key("nebiros").build()));
        verify(userEmailAuthRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("사용자 비밀번호 인증 입력 폼 체크")
    public void checkUserPasswordAuth() {
        // 인증키가 없거나 만료된 경우
        when(userPasswordAuthRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertEquals(ServiceExceptionMessage.AUTH_KEY_NOT_FOUND.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserPasswordAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").build(),
                                UserPasswordInput.builder().email("nebi25@naver.com").password("sarami").rePassword("sarami").key("nebiros").build())).getMessage());

        // 인증키 정보가 올바르지 않은 경우 (널값)
        when(userPasswordAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserPasswordAuth.builder().updatePasswordAuthKey(null).build()));
        assertEquals(ServiceExceptionMessage.AUTH_VALID_KEY_MISMATCH.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserPasswordAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").build(),
                                UserPasswordInput.builder().email("nebi25@naver.com").password("sarami").rePassword("sarami").key("nebiros").build())).getMessage());

        // 인증키 정보가 올바르지 않은 경우 (사용자 입력값, 발급 인증키 정보 불일치)
        when(userPasswordAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserPasswordAuth.builder().updatePasswordAuthKey("dark night").build()));
        assertEquals(ServiceExceptionMessage.AUTH_VALID_KEY_MISMATCH.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserPasswordAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").build(),
                                UserPasswordInput.builder().email("nebi25@naver.com").password("sarami").rePassword("sarami").key("nebiros").build())).getMessage());

        // 비밀번호,재비밀번호 불일치
        when(userPasswordAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserPasswordAuth.builder().updatePasswordAuthKey("nebiros").build()));
        assertEquals(ServiceExceptionMessage.RE_PASSWORD_MISMATCH.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserPasswordAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").isAuth(true).build(),
                                UserPasswordInput.builder().email("nebi25@naver.com").key("nebiros").password("sarami").rePassword("namiss").build())).getMessage());

        // 비밀번호가 이전 비밀번호와 똑같은 경우
        when(userPasswordAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserPasswordAuth.builder().updatePasswordAuthKey("nebiros").build()));
        assertEquals(ServiceExceptionMessage.COINCIDE_WITH_EACH_PASSWORD.message(),
                assertThrows(UserAuthException.class,
                        () -> userAuthService.checkUserPasswordAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").password(BCrypt.hashpw("sarami", BCrypt.gensalt())).isAuth(true).build(),
                                UserPasswordInput.builder().email("nebi25@naver.com").key("nebiros").password("sarami").rePassword("sarami").build())).getMessage());

        // 정상적으로 인증
        when(userPasswordAuthRepository.findById(anyInt())).thenReturn(Optional.of(UserPasswordAuth.builder().updatePasswordAuthKey("nebiros").build()));
        when(userPasswordAuthRepository.save(any())).thenReturn(UserPasswordAuth.builder().build());
        assertDoesNotThrow(() -> userAuthService.checkUserPasswordAuth(UserDomain.builder().userId("nebi25").email("nebi25@naver.com").isAuth(false).password(BCrypt.hashpw("Ww44226003!@", BCrypt.gensalt())).build(),
                UserPasswordInput.builder().email("nebi25@naver.com").key("nebiros").password("sarami").rePassword("sarami").build()));
        verify(userPasswordAuthRepository, times(1)).save(any());
    }
}
