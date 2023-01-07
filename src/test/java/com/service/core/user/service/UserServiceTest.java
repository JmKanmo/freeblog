package com.service.core.user.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.email.service.EmailService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.SocialAddress;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.model.*;
import com.service.util.BlogUtil;
import com.service.util.aws.s3.AwsS3Service;
import com.service.util.sftp.SftpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    @MockBean
    private SftpService sftpService;

    @MockBean
    private AwsS3Service awsS3Service;

    @Test
    @DisplayName("회원가입 정상 진행 테스트")
    public void processSignUpNormalTest() {
        when(userInfoService.checkUserDomainByEmail(any())).thenReturn(false);
        when(userInfoService.checkUserDomainById(any())).thenReturn(false);
        when(blogService.register(any())).thenReturn(Blog.builder().build());
        doNothing().when(userInfoService).saveUserDomain(any());
        when(userAuthService.saveUserEmailAuth(anyInt())).thenReturn("");
        when(userAuthService.findUserEmailAuthKey(anyInt())).thenReturn("");
        when(emailService.sendSignUpMail(anyString(), anyString(), "https", BlogUtil.getCurrentIp(), 8400)).thenReturn(0L);

        assertDoesNotThrow(() -> userService.processSignUp(UserSignUpInput.builder().email("nebi25@naver.com").id("nebi25").build(), UserDomain.builder().email("nebi25@naver.com").userId("nebi25").build()));
        verify(userAuthService, times(1)).saveUserEmailAuth(anyInt());
        verify(userAuthService, times(1)).findUserEmailAuthKey(anyInt());
        verify(emailService, times(1)).sendSignUpMail(anyString(), anyString(), "https", BlogUtil.getCurrentIp(), 8400);
    }

    @Test
    @DisplayName("회원가입 예외 발생 진행 테스트")
    public void processSignUpExceptionTest() {
        when(userInfoService.checkUserDomainByEmail(any())).thenReturn(true);
        when(userInfoService.checkUserDomainById(any())).thenReturn(true);
        assertThrowsExactly(UserManageException.class, () -> userService.processSignUp(UserSignUpInput.builder().email("nebi25@naver.com").id("nebi25").build(), UserDomain.builder().email("nebi25@naver.com").userId("nebi25").build()));
        verify(userAuthService, times(0)).saveUserEmailAuth(anyInt());
        verify(userAuthService, times(0)).findUserEmailAuthKey(anyInt());
        verify(emailService, times(0)).sendSignUpMail(anyString(), anyString(), "https", BlogUtil.getCurrentIp(), 8400);
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
        assertEquals(ServiceExceptionMessage.NOT_AUTHENTICATED_ACCOUNT.message(), assertThrows(UserAuthException.class, () -> userService.checkIsActive(email)).getMessage());

        // 정지 사용자의 경우
        when(userInfoService.findUserDomainByEmailOrThrow(email)).
                thenReturn(UserDomain.builder().status(UserStatus.STOP).build());
        assertEquals(ServiceExceptionMessage.NOT_AUTHENTICATED_ACCOUNT.message(), assertThrows(UserAuthException.class, () -> userService.checkIsActive(email)).getMessage());

        // 탈퇴 사용자의 경우
        when(userInfoService.findUserDomainByEmailOrThrow(email)).
                thenReturn(UserDomain.builder().status(UserStatus.WITHDRAW).build());
        assertEquals(ServiceExceptionMessage.NOT_AUTHENTICATED_ACCOUNT.message(), assertThrows(UserAuthException.class, () -> userService.checkIsActive(email)).getMessage());

        // 미등록 회원의 경우
        when(userInfoService.findUserDomainByEmailOrThrow(email)).
                thenThrow(new UserAuthException((ServiceExceptionMessage.ACCOUNT_INFO_NOT_FOUND.message())));
        assertEquals(ServiceExceptionMessage.ACCOUNT_INFO_NOT_FOUND.message(), assertThrows(UsernameNotFoundException.class, () -> userService.checkIsActive(email)).getMessage());
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
        assertEquals(ServiceExceptionMessage.ALREADY_SAME_EMAIL.message(), assertThrows(UserManageException.class, () -> userService.checkSameUser(userDomain)).getMessage());

        // 이미 사용 중인 ID
        when(userInfoService.checkUserDomainByEmail(userDomain.getEmail())).thenReturn(false);
        when(userInfoService.checkUserDomainById(userDomain.getUserId())).thenReturn(true);
        assertEquals(ServiceExceptionMessage.ALREADY_SAME_ID.message(), assertThrows(UserManageException.class, () -> userService.checkSameUser(userDomain)).getMessage());
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
        assertEquals(ServiceExceptionMessage.ALREADY_SAME_ID.message(), assertThrows(UserManageException.class, () -> userService.checkSameId(id)).getMessage());
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
        assertEquals(ServiceExceptionMessage.ALREADY_SAME_EMAIL.message(), assertThrows(UserManageException.class, () -> userService.checkSameEmail(email)).getMessage());
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
    @DisplayName("이메일로 유저 세팅 정보 검색 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void findUserSettingDtoByEmail(String email) {
        when(userInfoService.findUserDomainByEmailOrElse(email, null)).thenReturn(UserDomain.builder().email(email).build());
        assertNotNull(userService.findUserSettingDtoByEmail(email));
        verify(userInfoService, times(1)).findUserDomainByEmailOrElse(email, null);
    }

    @ParameterizedTest
    @DisplayName("이메일로 유저 기본 정보 검색 테스트")
    @ValueSource(strings = "nebi25@naver.com")
    void findUserHeaderDtoByEmail(String email) {
        when(userInfoService.findUserDomainByEmailOrElse(email, null)).thenReturn(UserDomain.builder().email(email).build());
        assertNotNull(userService.findUserHeaderDtoByEmail(email));
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

        List<UserEmailFindDto> result = userService.findUserEmailFindDtoListByNickname(nickname);

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

    @Test
    @DisplayName("회원 등록 테스트")
    void register() {
        UserSignUpInput signUpInput = UserSignUpInput.builder()
                .id("nebi25")
                .build();
        UserDomain userDomain = UserDomain.builder().build();
        Blog blog = Blog.from(signUpInput);
        when(blogService.register(blog)).thenReturn(blog);
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        assertDoesNotThrow(() -> userService.register(signUpInput, userDomain));

        verify(blogService, times(1)).register(blog);
        verify(userInfoService, times(1)).saveUserDomain(userDomain);
    }

    @Test
    @DisplayName("회원탈퇴 테스트")
    void withdraw() {
        UserWithdrawInput userWithdrawInput = UserWithdrawInput.builder().id("nebi25").password("hello world").build();
        // 로그인 정보(이메일)가 다른 경우
        when(userInfoService.findUserDomainByIdOrThrow(userWithdrawInput.getId())).thenReturn(UserDomain.builder().email("apdh1709@gmail.com").password(BCrypt.hashpw("hello world", BCrypt.gensalt())).build());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("nebi25@naver.com");
        assertEquals(ServiceExceptionMessage.MISMATCH_EMAIL.message(), assertThrows(UserAuthException.class,
                () -> userService.withdraw(userWithdrawInput, authentication)).getMessage());

        // 로그인 정보(비밀번호)가 다른 경우
        UserWithdrawInput userWithdrawInput1 = UserWithdrawInput.builder().id("akxk25").password("hello world~").build();
        when(userInfoService.findUserDomainByIdOrThrow(userWithdrawInput1.getId())).thenReturn(
                UserDomain.builder().email("nebi25@naver.com").password(BCrypt.hashpw("yamedara", BCrypt.gensalt())).build());
        Authentication authentication1 = mock(Authentication.class);
        when(authentication1.getName()).thenReturn("nebi25@naver.com");
        assertEquals(ServiceExceptionMessage.MISMATCH_PASSWORD.message(), assertThrows(UserAuthException.class,
                () -> userService.withdraw(userWithdrawInput1, authentication1)).getMessage());


        // 정상적인 입력
        UserWithdrawInput userWithdrawInput2 = UserWithdrawInput.builder().id("akxk25").password("hello world~").build();
        UserDomain userDomain = UserDomain.builder().email("apdh1709@gmail.com").password(BCrypt.hashpw("hello world~", BCrypt.gensalt())).build();
        when(userInfoService.findUserDomainByIdOrThrow(userWithdrawInput2.getId())).thenReturn(userDomain);
        Authentication authentication2 = mock(Authentication.class);
        when(authentication2.getName()).thenReturn("apdh1709@gmail.com");
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        assertDoesNotThrow(() -> userService.withdraw(userWithdrawInput2, authentication2));
        assertEquals(userDomain.getStatus(), UserStatus.WITHDRAW);
        assertTrue(userDomain.getWithdrawTime() != null);
        verify(userInfoService, times(1)).saveUserDomain(userDomain);
    }

    @Test
    @DisplayName("기본 정보 수정 테스트")
    public void updateBasicInfo() {
        UserBasicInfoInput userBasicInfoInput = UserBasicInfoInput.builder()
                .id("nebi25").blogName("nebi's blog")
                .greetings("hello").nickname("nebiros")
                .intro("https://play.afreecatv.com/kimseah94/242129288").build();

        Blog blog = Blog.builder().build();
        UserDomain userDomain = UserDomain.builder().blog(blog).build();

        when(userInfoService.findUserDomainByIdOrThrow(userBasicInfoInput.getId())).thenReturn(userDomain);
        when(blogService.register(blog)).thenReturn(blog);
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        assertDoesNotThrow(() -> userService.updateUserBasicInfo(userBasicInfoInput, null));

        assertTrue(blog.getName().equals(userBasicInfoInput.getBlogName()));
        assertTrue(blog.getIntro().equals(userBasicInfoInput.getIntro()));
        assertTrue(userDomain.getNickname().equals(userBasicInfoInput.getNickname()));
        assertTrue(userDomain.getGreetings().equals(userBasicInfoInput.getGreetings()));
        assertTrue(Objects.equals(userDomain.getBlog(), blog));

        verify(blogService, times(1)).register(blog);
        verify(userInfoService, times(1)).saveUserDomain(userDomain);
    }

    @Test
    @DisplayName("소셜 정보 수정 테스트")
    public void updateSocialAddress() {
        UserSocialAddressInput userSocialAddressInput = UserSocialAddressInput.builder()
                .id("nebi25")
                .address("https://blog/nebi25")
                .github("https://github.com/JmKanmo")
                .twitter("https://twitter.com/nebi25")
                .instagram("https://instagram.com/nebi25")
                .build();

        SocialAddress socialAddress = SocialAddress.builder().build();
        UserDomain userDomain = UserDomain.builder()
                .socialAddress(socialAddress)
                .build();
        when(userInfoService.findUserDomainByIdOrThrow(userSocialAddressInput.getId())).thenReturn(userDomain);
        assertDoesNotThrow(() -> userService.updateUserSocialAddress(userSocialAddressInput));
        assertTrue(socialAddress.getAddress().equals(userSocialAddressInput.getAddress()));
        assertTrue(socialAddress.getGithub().equals(userSocialAddressInput.getGithub()));
        assertTrue(socialAddress.getTwitter().equals(userSocialAddressInput.getTwitter()));
        assertTrue(socialAddress.getInstagram().equals(userSocialAddressInput.getInstagram()));
        verify(userInfoService, times(1)).findUserDomainByIdOrThrow(userSocialAddressInput.getId());
    }

    @ParameterizedTest
    @DisplayName("SFTP를 이용한 프로필 이미지 업로드 테스트")
    @ValueSource(strings = "nebi25")
    public void uploadSftpProfileImageById(String id) throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String profileImageSrc = "http://53.14.34.26/3fsdfskdfkjgkldfjglkkfdmbfgd.gif";
        UserDomain userDomain = UserDomain.builder().userId(id).build();

        when(sftpService.sftpFileUpload(multipartFile)).thenReturn(profileImageSrc);
        when(userInfoService.findUserDomainByIdOrThrow(id)).thenReturn(userDomain);
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        assertDoesNotThrow(() -> userService.uploadSftpProfileImageById(multipartFile, id));
        assertTrue(userDomain.getProfileImage().equals(profileImageSrc));

        verify(sftpService, times(1)).sftpFileUpload(multipartFile);
        verify(userInfoService, times(1)).findUserDomainByIdOrThrow(id);
        verify(userInfoService, times(1)).saveUserDomain(userDomain);
    }

    @ParameterizedTest
    @DisplayName("AWS S3를 이용한 프로필 이미지 업로드 테스트")
    @ValueSource(strings = "nebi25")
    public void uploadAwsS3ProfileImageById(String id) throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        String profileImageSrc = "https://freelog-s3-bucket.s3.amazonaws.com/image/3fsdfskdfkjgkldfjglkkfdmbfgd.gif";
        UserDomain userDomain = UserDomain.builder().userId(id).build();

        when(awsS3Service.uploadImageFile(multipartFile)).thenReturn(profileImageSrc);
        when(userInfoService.findUserDomainByIdOrThrow(id)).thenReturn(userDomain);
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        assertDoesNotThrow(() -> userService.uploadAwsS3ProfileImageById(multipartFile, id, mock(Principal.class)));
        assertTrue(userDomain.getProfileImage().equals(profileImageSrc));

        verify(awsS3Service, times(1)).uploadImageFile(multipartFile);
        verify(userInfoService, times(1)).findUserDomainByIdOrThrow(id);
        verify(userInfoService, times(1)).saveUserDomain(userDomain);
    }

    @ParameterizedTest
    @DisplayName("프로필 이미지 삭제 테스트")
    @ValueSource(strings = "nebi25")
    public void removeProfileImageById(String id) {
        // TODO
        UserDomain userDomain = UserDomain.builder().userId(id).profileImage("https://freelog-s3-bucket.s3.amazonaws.com/image/3fsdfskdfkjgkldfjglkkfdmbfgd.gif").build();
        when(userInfoService.findUserDomainByIdOrThrow(id)).thenReturn(userDomain);
        doNothing().when(userInfoService).saveUserDomain(userDomain);
        assertDoesNotThrow(() -> userService.removeProfileImageById(id, mock(Principal.class)));
        assertTrue(userDomain.getProfileImage() == null);
        verify(userInfoService, times(1)).saveUserDomain(userDomain);
    }
}