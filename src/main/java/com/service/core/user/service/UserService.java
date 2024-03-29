package com.service.core.user.service;

import com.service.core.blog.domain.Blog;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.dto.*;
import com.service.core.user.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface UserService extends UserDetailsService {
    void processSignUp(UserSignUpInput signupForm, UserDomain userDomain);

    void register(UserSignUpInput signupForm, UserDomain userDomain);

    void withdraw(UserWithdrawInput userWithdrawInput, Authentication authentication);

    boolean checkIsActive(String email);

    boolean checkIsAuthenticated(String email);

    boolean checkSameUser(UserDomain user);

    boolean checkSameId(String id);

    boolean checkSameEmail(String email);

    boolean checkExistUserId(String id);

    void emailAuth(UserAuthInput userAuthInput);

    String updateEmailAuthCondition(String email);

    String updatePasswordAuthCondition(String email);

    void updatePassword(UserPasswordInput userPasswordInput, Principal principal);

    UserHeaderDto updateUserBasicInfo(UserBasicInfoInput userBasicInfoInput, Principal principal);

    void updateUserSocialAddress(UserSocialAddressInput userSocialAddressInput, Principal principal);

    List<UserEmailFindDto> findUserEmailFindDtoListByNickname(String nickname);

    UserSettingDto findUserSettingDtoByEmail(String email);

    UserHeaderDto findUserHeaderDtoByEmail(String email);

    UserCommentDto findUserCommentDtoByEmail(String email);

    UserProfileDto findUserProfileDtoById(String id);

    UserProfileDto findUserProfileDtoByBlogId(Long blogId);

    String uploadSftpProfileImageById(MultipartFile multipartFile, String id) throws Exception;

    UserHeaderDto uploadThumbnailProfileImageById(MultipartFile multipartFile, String id, String uploadType, String uploadKey, Principal principal) throws Exception;

    UserHeaderDto removeProfileImageById(String id, Principal principal);
}
