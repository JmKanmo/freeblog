package com.service.core.user.service.impl;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.email.service.EmailService;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.SocialAddress;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserBasicDto;
import com.service.core.user.dto.UserDto;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.dto.UserSettingDto;
import com.service.core.user.model.*;
import com.service.core.user.service.UserAuthService;
import com.service.core.user.service.UserInfoService;
import com.service.core.user.service.UserService;
import com.service.util.ConstUtil;
import com.service.util.BlogUtil;
import com.service.util.sftp.SftpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EmailService emailService;
    private final BlogService blogService;
    private final UserInfoService userInfoService;
    private final UserAuthService userAuthService;

    private final SftpService sftpService;

    @Override
    public void processSignUp(UserSignUpInput signupForm, UserDomain userDomain) {
        if (checkSameUser(userDomain)) {
            register(signupForm, userDomain);
            userAuthService.saveUserEmailAuth(BlogUtil.createUserAuthId(userDomain));
            emailService.sendSignUpMail(signupForm.getEmail(), userAuthService.findUserEmailAuthKey(BlogUtil.createUserAuthId(userDomain)));
        }
    }

    @Transactional
    @Override
    public void register(UserSignUpInput signupForm, UserDomain userDomain) {
        Blog blog = blogService.register(Blog.from(signupForm));
        userDomain.setBlog(blog);
        userInfoService.saveUserDomain(userDomain);
    }

    @Override
    public void withdraw(UserWithdrawInput userWithdrawInput, Authentication authentication) {
        UserDomain userDomain = userInfoService.findUserDomainByIdOrThrow(userWithdrawInput.getId());

        if (!userDomain.getEmail().equals(authentication.getName())) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.MISMATCH_EMAIL);
        } else if (!BCrypt.checkpw(userWithdrawInput.getPassword(), userDomain.getPassword())) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.MISMATCH_PASSWORD);
        }

        userDomain.setStatus(UserStatus.WITHDRAW);
        userDomain.setWithdrawTime(LocalDateTime.now());
        userInfoService.saveUserDomain(userDomain);
    }

    @Override
    public boolean checkIsActive(String email) {
        UserDomain user = userInfoService.findUserDomainByEmailOrThrow(email);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.NOT_AUTHENTICATED_ACCOUNT);
        }

        return true;
    }

    @Override
    public boolean checkSameUser(UserDomain user) {
        if (userInfoService.checkUserDomainByEmail(user.getEmail())) {
            throw new UserManageException(ConstUtil.ExceptionMessage.ALREADY_SAME_EMAIL);
        }
        if (userInfoService.checkUserDomainById(user.getUserId())) {
            throw new UserManageException(ConstUtil.ExceptionMessage.ALREADY_SAME_ID);
        }

        return true;
    }

    @Override
    public boolean checkSameId(String id) {
        if (userInfoService.checkUserDomainById(id)) {
            throw new UserManageException(ConstUtil.ExceptionMessage.ALREADY_SAME_ID);
        }
        return true;
    }

    @Override
    public boolean checkSameEmail(String email) {
        if (userInfoService.checkUserDomainByEmail(email)) {
            throw new UserManageException(ConstUtil.ExceptionMessage.ALREADY_SAME_EMAIL);
        }
        return true;
    }

    @Override
    public void emailAuth(UserAuthInput userAuthInput) {
        UserDomain user = userInfoService.findUserDomainByEmailOrThrow(userAuthInput.getEmail());

        if (userAuthService.checkUserEmailAuth(user, userAuthInput)) {
            user.setStatus(UserStatus.ACTIVE);
            user.setAuth(true);
            userInfoService.saveUserDomain(user);
        }
    }

    @Override
    public String updateEmailAuthCondition(String email) {
        UserDomain user = userInfoService.findUserDomainByEmailOrThrow(email);
        userInfoService.saveUserDomain(user);
        String emailAuthKey = userAuthService.saveUserEmailAuth(BlogUtil.createUserAuthId(user));
        return emailAuthKey;
    }

    @Override
    public String updatePasswordAuthCondition(String email) {
        UserDomain user = userInfoService.findUserDomainByEmailOrThrow(email);
        userInfoService.saveUserDomain(user);
        String passwordAuthKey = userAuthService.saveUserPasswordAuth(BlogUtil.createUserAuthId(user));
        return passwordAuthKey;
    }

    @Override
    public void updatePassword(UserPasswordInput userPasswordInput) {
        UserDomain user = userInfoService.findUserDomainByEmailOrThrow(userPasswordInput.getEmail());

        if (userAuthService.checkUserPasswordAuth(user, userPasswordInput)) {
            user.setPassword(BCrypt.hashpw(userPasswordInput.getPassword(), BCrypt.gensalt()));
            user.setPasswordUpdateTime(LocalDateTime.now());
            userInfoService.saveUserDomain(user);
        }
    }

    @Transactional
    @Override
    public void updateUserBasicInfo(UserBasicInfoInput userBasicInfoInput) {
        UserDomain user = userInfoService.findUserDomainByIdOrThrow(userBasicInfoInput.getId());
        Blog blog = user.getBlog();
        blog.setName(userBasicInfoInput.getBlogName());
        blog.setIntro(userBasicInfoInput.getIntro());
        blogService.register(blog);
        user.setNickname(userBasicInfoInput.getNickname());
        user.setGreetings(userBasicInfoInput.getGreetings());
        user.setBlog(blog);
        userInfoService.saveUserDomain(user);
    }

    @Override
    public void updateUserSocialAddress(UserSocialAddressInput userSocialAddressInput) {
        UserDomain userDomain = userInfoService.findUserDomainByIdOrThrow(userSocialAddressInput.getId());
        SocialAddress socialAddress = userDomain.getSocialAddress();

        socialAddress.setAddress(userSocialAddressInput.getAddress());
        socialAddress.setGithub(userSocialAddressInput.getGithub());
        socialAddress.setTwitter(userSocialAddressInput.getTwitter());
        socialAddress.setInstagram(userSocialAddressInput.getInstagram());

        userInfoService.saveUserDomain(userDomain);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserDomain user = userInfoService.findUserDomainByEmailOrThrow(email);

        BlogUtil.checkUserStatus(user.getStatus());

        List<GrantedAuthority> grantedAuthorityList = new LinkedList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(user.getEmail(), user.getPassword(), grantedAuthorityList);
    }

    @Override
    public List<UserEmailFindDto> findUserEmailFindDtoListByNickname(String nickname) {
        List<UserEmailFindDto> userEmailFindDtoList = userInfoService.findUsersByNickName(nickname);
        userEmailFindDtoList.forEach(userEmailFindDto -> {
            userEmailFindDto.setEmail(BlogUtil.encryptEmail(userEmailFindDto.getEmail()));
        });
        return userEmailFindDtoList;
    }

    @Transactional(readOnly = true)
    @Override
    public UserSettingDto findUserSettingDtoByEmail(String email) {
        return UserSettingDto.fromEntity(userInfoService.findUserDomainByEmailOrElse(email, null));
    }

    @Override
    public UserDto findUserDtoByEmail(String email) {
        return UserDto.fromEntity(userInfoService.findUserDomainByEmailOrElse(email, null));
    }

    @Override
    public UserBasicDto findUserBasicDtoByEmail(String email) {
        return UserBasicDto.fromEntity(userInfoService.findUserDomainByEmailOrElse(email, null));
    }

    @Override
    public String uploadProfileImageById(MultipartFile multipartFile, String id) throws Exception {
        try {
            String profileImageSrc = sftpService.sftpFileUpload(multipartFile);
            UserDomain userDomain = userInfoService.findUserDomainByIdOrThrow(id);
            userDomain.setProfileImage(profileImageSrc);
            userInfoService.saveUserDomain(userDomain);
            return profileImageSrc;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void removeProfileImageById(String id) {
        UserDomain userDomain = userInfoService.findUserDomainByIdOrThrow(id);
        userDomain.setProfileImage(null);
        userInfoService.saveUserDomain(userDomain);
    }
}
