package com.service.core.user.service;

import com.service.config.UserAuthValidTimeConfig;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import com.service.core.user.model.UserStatus;
import com.service.core.user.repository.UserRepository;
import com.service.core.user.repository.mapper.UserMapper;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private UserAuthValidTimeConfig userAuthValidTimeConfig;


    @ParameterizedTest
    @DisplayName("특정 이메일의 사용자 활성상태 여부 체크 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void checkIsActive(String email) {
        // ACTIVE(인증 완료) 사용자의 경우
        when(userRepository.findByEmail(email)).
                thenReturn(Optional.of(UserDomain.builder().status(UserStatus.ACTIVE).build()));

        assertTrue(userService.checkIsActive(email));

        // 비인증 사용자의 경우
        when(userRepository.findByEmail(email)).
                thenReturn(Optional.of(UserDomain.builder().status(UserStatus.NOT_AUTH).build()));
        assertEquals(ConstUtil.ExceptionMessage.NOT_AUTHENTICATED_USER.message(), assertThrows(UserAuthException.class, () -> userService.checkIsActive(email)).getMessage());

        // 미등록 회원의 경우
        when(userRepository.findByEmail(email)).
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
        when(userRepository.findByEmail(userDomain.getEmail())).
                thenReturn(Optional.empty());
        when(userRepository.findById(userDomain.getUserId())).thenReturn(Optional.empty());
        assertTrue(userService.checkSameUser(userDomain));

        // 이미 사용 중인 이메일
        when(userRepository.findByEmail(userDomain.getEmail())).
                thenReturn(Optional.of(UserDomain.builder().status(UserStatus.ACTIVE).build()));
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_SAME_EMAIL.message(), assertThrows(UserManageException.class, () -> userService.checkSameUser(userDomain)).getMessage());
        // 이미 사용 중인 ID
        when(userRepository.findByEmail(userDomain.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findById(userDomain.getUserId())).thenReturn(Optional.of(UserDomain.builder().userId(userDomain.getUserId()).build()));
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_SAME_ID.message(), assertThrows(UserManageException.class, () -> userService.checkSameUser(userDomain)).getMessage());
    }

    @ParameterizedTest
    @DisplayName("특정 ID의 사용자 존재 여부 테스트")
    @ValueSource(strings = "nebi25")
    void checkSameId(String id) {
        // 사용 가능 ID
        when(userRepository.findById(id)).
                thenReturn(Optional.empty());
        assertTrue(userService.checkSameId(id));

        // 이미 존재하는 ID
        when(userRepository.findById(id)).thenReturn(Optional.of(UserDomain.builder().userId(id).build()));
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_SAME_ID.message(), assertThrows(UserManageException.class, () -> userService.checkSameId(id)).getMessage());
    }

    @ParameterizedTest
    @DisplayName("특정 이메일의 사용자 존재 여부 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void checkSameEmail(String email) {
        // 사용 가능 이메일
        when(userRepository.findByEmail(email)).
                thenReturn(Optional.empty());
        assertTrue(userService.checkSameEmail(email));

        // 이미 존재하는 이메일
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(UserDomain.builder().email(email).build()));
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_SAME_EMAIL.message(), assertThrows(UserManageException.class, () -> userService.checkSameEmail(email)).getMessage());
    }

    @ParameterizedTest
    @DisplayName("이메일 인증 테스트")
    @CsvSource("FREELOG_KEY, nebi25@naver.com")
    void emailAuth(String key, String email) {
        // 정상 입력
        UserAuthInput normalUserAuthInput = UserAuthInput.builder().key(key).email(email).build();
        UserDomain normalUserDomain = UserDomain.builder().authKey(normalUserAuthInput.getKey())
                .email(normalUserAuthInput.getEmail())
                .isAuth(false)
                .authExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getEmailAuthValidTime() - 1)).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(normalUserDomain));
        when(userRepository.save(normalUserDomain)).thenReturn(normalUserDomain);
        assertDoesNotThrow(() -> userService.emailAuth(normalUserAuthInput));
        verify(userRepository, times(1)).save(normalUserDomain);

        // 잘못된 사용자 이메일
        UserAuthInput nonExistUserAuthInput = UserAuthInput.builder().key(key).email("<<undefined>>").build();
        UserDomain nonExistUserDomain = UserDomain.builder().authKey(nonExistUserAuthInput.getKey())
                .email(nonExistUserAuthInput.getEmail())
                .isAuth(false)
                .authExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getEmailAuthValidTime() - 1)).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(nonExistUserDomain));
        when(userRepository.save(nonExistUserDomain)).thenReturn(nonExistUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.USER_INFO_NOT_FOUND.message(), assertThrows(UsernameNotFoundException.class, () -> userService.emailAuth(nonExistUserAuthInput)).getMessage());
        verify(userRepository, times(0)).save(nonExistUserDomain);

        // 잘못된 키 입력
        UserAuthInput unNormalKeyUserAuthInput = UserAuthInput.builder().key(key).email(email).build();
        UserDomain unNormalKeyUserDomain = UserDomain.builder().authKey("mismatch key")
                .email(unNormalKeyUserAuthInput.getEmail())
                .isAuth(false)
                .authExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getEmailAuthValidTime() - 1)).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(unNormalKeyUserDomain));
        when(userRepository.save(unNormalKeyUserDomain)).thenReturn(unNormalKeyUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.AUTH_VALID_KEY_MISMATCH.message(), assertThrows(UserAuthException.class, () -> userService.emailAuth(unNormalKeyUserAuthInput)).getMessage());
        verify(userRepository, times(0)).save(unNormalKeyUserDomain);

        // 한참 지난 시간 입력
        UserAuthInput unNormalTimeUserAuthInput = UserAuthInput.builder().key(key).email(email).build();
        UserDomain unNormalTimeUserDomain = UserDomain.builder().authKey(unNormalTimeUserAuthInput.getKey())
                .email(unNormalTimeUserAuthInput.getEmail())
                .isAuth(false)
                .authExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getEmailAuthValidTime() + 100)).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(unNormalTimeUserDomain));
        when(userRepository.save(unNormalTimeUserDomain)).thenReturn(unNormalTimeUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.AUTH_VALID_TIME_EXPIRED.message(), assertThrows(UserAuthException.class, () -> userService.emailAuth(unNormalTimeUserAuthInput)).getMessage());
        verify(userRepository, times(0)).save(unNormalTimeUserDomain);

        // 이미 인증 된 사용자
        UserAuthInput alreadyAuthInput = UserAuthInput.builder().key(key).email(email).build();
        UserDomain alreadyAuthUserDomain = UserDomain.builder().authKey(alreadyAuthInput.getKey())
                .email(alreadyAuthInput.getEmail())
                .isAuth(true)
                .authExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getEmailAuthValidTime())).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(alreadyAuthUserDomain));
        when(userRepository.save(alreadyAuthUserDomain)).thenReturn(alreadyAuthUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.ALREADY_AUTHENTICATED_USER.message(), assertThrows(UserAuthException.class, () -> userService.emailAuth(alreadyAuthInput)).getMessage());
        verify(userRepository, times(0)).save(alreadyAuthUserDomain);
    }

    @ParameterizedTest
    @DisplayName("닉네임에 해당하는 유저 체크 테스트")
    @CsvSource("nebi25, nebi25@naver.com")
    void findUsersByNickname(String nickname, String email) {
        List<UserEmailFindDto> userEmailFindDtoList = Arrays.asList(UserEmailFindDto.builder().nickname(nickname).email(email).build()
                , UserEmailFindDto.builder().nickname(nickname).email(email).build()
                , UserEmailFindDto.builder().nickname(nickname).email(email).build());
        // 존재하는 id
        when(userMapper.findUsersByNickName(nickname)).thenReturn(userEmailFindDtoList);

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
        UserDomain userDomain = UserDomain.builder().email(email).build();
        // 사용 가능 이메일
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userDomain));
        when(userRepository.save(userDomain)).thenReturn(userDomain);
        assertNotNull(userService.updateEmailAuthCondition(email));
        verify(userRepository, times(1)).save(userDomain);

        // 존재하지 않는 이메일
        UserDomain nonExistUserDomain = UserDomain.builder().email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(nonExistUserDomain)).thenReturn(nonExistUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.USER_INFO_NOT_FOUND.message(), assertThrows(UsernameNotFoundException.class, () -> userService.updateEmailAuthCondition(email)).getMessage());
        verify(userRepository, times(0)).save(nonExistUserDomain);
    }

    @ParameterizedTest
    @DisplayName("비밀번호 변경 인증 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void updatePasswordAuthCondition(String email) {
        UserDomain userDomain = UserDomain.builder().email(email).build();
        // 사용 가능 이메일
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userDomain));
        when(userRepository.save(userDomain)).thenReturn(userDomain);
        assertNotNull(userService.updatePasswordAuthCondition(email));
        verify(userRepository, times(1)).save(userDomain);

        // 존재하지 않는 이메일
        UserDomain nonExistUserDomain = UserDomain.builder().email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(nonExistUserDomain)).thenReturn(nonExistUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.USER_INFO_NOT_FOUND.message(), assertThrows(UsernameNotFoundException.class, () -> userService.updatePasswordAuthCondition(email)).getMessage());
        verify(userRepository, times(0)).save(nonExistUserDomain);
    }

    @Test
    void updatePassword() {
        // 정상 입력
        UserPasswordInput userPasswordInput = UserPasswordInput.builder().email("nebi25@naver.com")
                .key("freelog-authentication-key").password("sdkels12!@").rePassword("sdkels12!@").build();

        UserDomain userDomain = UserDomain.builder()
                .updatePasswordKey(userPasswordInput.getKey())
                .updatePasswordExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getUpdatePasswordValidTime()))
                .build();

        when(userRepository.findByEmail(userPasswordInput.getEmail())).thenReturn(Optional.of(userDomain));
        when(userRepository.save(userDomain)).thenReturn(userDomain);
        assertDoesNotThrow(() -> userService.updatePassword(userPasswordInput));
        verify(userRepository, times(1)).save(userDomain);

        // 이메일이 잘못된 경우
        UserPasswordInput nonExistEmailUserPasswordInput = UserPasswordInput.builder().email("<<undefined>>")
                .key("freelog-authentication-key").password("sdkels12!@").rePassword("sdkels12!@").build();

        UserDomain nonExistUserDomain = UserDomain.builder()
                .updatePasswordKey(userPasswordInput.getKey())
                .updatePasswordExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getUpdatePasswordValidTime()))
                .build();
        when(userRepository.findByEmail(nonExistEmailUserPasswordInput.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(nonExistUserDomain)).thenReturn(nonExistUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.USER_INFO_NOT_FOUND.message(), assertThrows(UsernameNotFoundException.class, () -> userService.updatePassword(nonExistEmailUserPasswordInput)).getMessage());
        verify(userRepository, times(0)).save(nonExistUserDomain);

        // 발급키가 잘못된 경우
        UserPasswordInput authKeyUserPasswordInput = UserPasswordInput.builder().email("<<undefined>>")
                .key("freelog-authentication-key").password("sdkels12!@").rePassword("sdkels12!@").build();

        UserDomain authKeyUserDomain = UserDomain.builder()
                .updatePasswordKey("freelog-mismatched-key")
                .updatePasswordExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getUpdatePasswordValidTime()))
                .build();

        when(userRepository.findByEmail(authKeyUserPasswordInput.getEmail())).thenReturn(Optional.of(authKeyUserDomain));
        when(userRepository.save(authKeyUserDomain)).thenReturn(authKeyUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.AUTH_VALID_KEY_MISMATCH.message(), assertThrows(UserAuthException.class, () -> userService.updatePassword(authKeyUserPasswordInput)).getMessage());

        // 패스워드 발급키 기한이 지난 경우
        UserPasswordInput pwExpireUserPasswordInput = UserPasswordInput.builder().email("<<undefined>>")
                .key("freelog-authentication-key").password("sdkels12!@").rePassword("sdkels12!@").build();

        UserDomain pwExpireUserDomain = UserDomain.builder()
                .updatePasswordKey(pwExpireUserPasswordInput.getKey())
                .updatePasswordExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getUpdatePasswordValidTime() + 10))
                .build();

        when(userRepository.findByEmail(pwExpireUserPasswordInput.getEmail())).thenReturn(Optional.of(pwExpireUserDomain));
        when(userRepository.save(pwExpireUserDomain)).thenReturn(pwExpireUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.AUTH_VALID_TIME_EXPIRED.message(), assertThrows(UserAuthException.class, () -> userService.updatePassword(pwExpireUserPasswordInput)).getMessage());

        // 비밀번호 <-> 재비밀번호 입력이 다른 경우
        UserPasswordInput pwDiffUserPasswordInput = UserPasswordInput.builder().email("<<undefined>>")
                .key("freelog-authentication-key").password("sdkels12!@").rePassword("Fddd233242fds").build();

        UserDomain pwDiffUserDomain = UserDomain.builder()
                .updatePasswordKey(pwDiffUserPasswordInput.getKey())
                .updatePasswordExpireDateTime(LocalDateTime.now().plusHours(userAuthValidTimeConfig.getUpdatePasswordValidTime()))
                .build();

        when(userRepository.findByEmail(pwDiffUserPasswordInput.getEmail())).thenReturn(Optional.of(pwDiffUserDomain));
        when(userRepository.save(pwDiffUserDomain)).thenReturn(pwDiffUserDomain);
        assertEquals(ConstUtil.ExceptionMessage.RE_PASSWORD_MISMATCH.message(), assertThrows(UserAuthException.class, () -> userService.updatePassword(pwDiffUserPasswordInput)).getMessage());
    }

    @ParameterizedTest
    @DisplayName("이메일로 유저 검색 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void findUserByEmail(String email) {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(UserDomain.builder().email(email).build()));
        assertNotNull(userService.findUserByEmail(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void register() {
        UserDomain userDomain = UserDomain.builder().build();
        when(userRepository.save(userDomain)).thenReturn(userDomain);
        assertDoesNotThrow(() -> userService.register(userDomain));
        verify(userRepository, times(1)).save(userDomain);
    }
}