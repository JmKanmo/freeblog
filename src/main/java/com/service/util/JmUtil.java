package com.service.util;

import com.service.core.error.model.UserAuthException;
import com.service.core.user.model.UserStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;

public class JmUtil {
    public static final String AUTHENTICATION_MESSAGE = "FLASH_AUTHENTICATION_MESSAGE";

    public static String getLoginFailMessage(AuthenticationException exception) {
        if (exception instanceof AuthenticationServiceException) {
            return ConstUtil.USER_INFO_NOT_FOUND;

        } else if (exception instanceof BadCredentialsException) {
            return ConstUtil.ID_PW_WRONG;

        } else if (exception instanceof LockedException) {
            return ConstUtil.LOCK_ACCOUNT;

        } else if (exception instanceof DisabledException) {
            return ConstUtil.DEACTIVATE_ACCOUNT;

        } else if (exception instanceof AccountExpiredException) {
            return ConstUtil.EXPIRED_ACCOUNT;

        } else if (exception instanceof CredentialsExpiredException) {
            return ConstUtil.EXPIRED_PASSWORD;
        }
        return exception.getMessage();
    }

    public static boolean checkUserStatus(UserStatus userStatus) {
        switch (userStatus) {
            case WITHDRAW:
                throw new UserAuthException(ConstUtil.WITHDRAW_USER);

            case STOP:
                throw new UserAuthException(ConstUtil.STOP_USER);
        }
        return true;
    }
}
