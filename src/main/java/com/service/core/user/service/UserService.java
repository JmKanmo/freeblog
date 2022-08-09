package com.service.core.user.service;

import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.UserBasicDto;
import com.service.core.user.dto.UserDto;
import com.service.core.user.dto.UserEmailFindDto;
import com.service.core.user.dto.UserSettingDto;
import com.service.core.user.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends UserDetailsService {
    void processSignUp(UserSignUpInput signupForm, UserDomain userDomain);

    void register(UserSignUpInput signupForm, UserDomain userDomain);

    void withdraw(UserWithdrawInput userWithdrawInput, Authentication authentication);

    boolean checkIsActive(String email);

    boolean checkSameUser(UserDomain user);

    boolean checkSameId(String id);

    boolean checkSameEmail(String email);

    void emailAuth(UserAuthInput userAuthInput);

    String updateEmailAuthCondition(String email);

    String updatePasswordAuthCondition(String email);

    void updatePassword(UserPasswordInput userPasswordInput);

    void updateUserBasicInfo(UserBasicInfoInput userBasicInfoInput);

    void updateUserSocialAddress(UserSocialAddressInput userSocialAddressInput);

    List<UserEmailFindDto> findUserEmailFindDtoListByNickname(String nickname);

    UserDto findUserDtoByEmail(String email);

    UserSettingDto findUserSettingDtoByEmail(String email);

    UserBasicDto findUserBasicDtoByEmail(String email);

    String uploadProfileImageById(MultipartFile multipartFile, String id) throws Exception;

    void removeProfileImageById(String id);
}
