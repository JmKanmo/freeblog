package com.service.core.user.service;

import com.service.core.user.domain.UserDomain;
import com.service.core.user.model.UserAuthInput;
import com.service.core.user.model.UserPasswordInput;

public interface UserAuthService {
    String saveUserEmailAuth(int id);

    String saveUserPasswordAuth(int id);

    String findUserEmailAuthKey(int id);

    boolean checkUserEmailAuth(UserDomain userDomain, UserAuthInput userAuthInput);

    boolean checkUserPasswordAuth(UserDomain userDomain, UserPasswordInput userPasswordInput);
}
