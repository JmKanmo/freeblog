package com.service.core.user.service.impl;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.service.BlogService;
import com.service.core.email.service.EmailService;
import com.service.core.error.model.UserAuthException;
import com.service.core.error.model.UserManageException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserDto;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.model.UserStatus;
import com.service.core.user.repository.UserRepository;
import com.service.core.user.repository.mapper.UserMapper;
import com.service.core.user.service.UserAuthService;
import com.service.core.user.service.UserInfoService;
import com.service.core.user.service.UserService;
import com.service.util.ConstUtil;
import com.service.util.BlogUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EmailService emailService;
    private final BlogService blogService;
    private final UserInfoService userInfoService;
    private final UserAuthService userAuthService;

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
    public boolean checkIsActive(String email) {
        UserDomain user = userInfoService.findUserDomainByEmailOrThrow(email);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserAuthException(ConstUtil.ExceptionMessage.NOT_AUTHENTICATED_USER);
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
    public List<UserEmailFindDto> findUsersByNickname(String nickname) {
        List<UserEmailFindDto> userEmailFindDtoList = userInfoService.findUsersByNickName(nickname);
        userEmailFindDtoList.forEach(userEmailFindDto -> {
            userEmailFindDto.setEmail(BlogUtil.encryptEmail(userEmailFindDto.getEmail()));
        });
        return userEmailFindDtoList;
    }

    @Override
    public UserDto findUserByEmail(String email) {
        return UserDto.fromEntity(userInfoService.findUserDomainByEmailOrElse(email, null));
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
            user.setUpdateTime(LocalDateTime.now());
            userInfoService.saveUserDomain(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserDomain user = userInfoService.findUserDomainByEmailOrThrow(email);

        BlogUtil.checkUserStatus(user.getStatus());

        List<GrantedAuthority> grantedAuthorityList = new LinkedList<>();
        grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(user.getEmail(), user.getPassword(), grantedAuthorityList);
    }
}
