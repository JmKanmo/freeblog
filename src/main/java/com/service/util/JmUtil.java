package com.service.util;

import com.service.core.error.model.UserAuthException;
import com.service.core.user.model.UserStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

public class JmUtil {
    public static String getLoginFailMessage(AuthenticationException exception) {
        if (exception instanceof AuthenticationServiceException) {
            return ConstUtil.UserAuthMessage.USER_INFO_NOT_FOUND;

        } else if (exception instanceof BadCredentialsException) {
            return ConstUtil.UserAuthMessage.ID_PW_WRONG;

        } else if (exception instanceof LockedException) {
            return ConstUtil.UserAuthMessage.LOCK_ACCOUNT;

        } else if (exception instanceof DisabledException) {
            return ConstUtil.UserAuthMessage.DEACTIVATE_ACCOUNT;

        } else if (exception instanceof AccountExpiredException) {
            return ConstUtil.UserAuthMessage.EXPIRED_ACCOUNT;

        } else if (exception instanceof CredentialsExpiredException) {
            return ConstUtil.UserAuthMessage.EXPIRED_PASSWORD;
        }
        return exception.getMessage();
    }

    public static boolean checkUserStatus(UserStatus userStatus) {
        switch (userStatus) {
            case WITHDRAW:
                throw new UserAuthException(ConstUtil.UserAuthMessage.WITHDRAW_USER);

            case STOP:
                throw new UserAuthException(ConstUtil.UserAuthMessage.STOP_USER);
        }
        return true;
    }

    public static String ofNull(String str) {
        return str == null ? ConstUtil.UNDEFINED : str;
    }

    public static String getImageFileUUID(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uuid = LocalDateTime.now().toString() + UUID.nameUUIDFromBytes(fileName.getBytes(StandardCharsets.UTF_8)) + "." + extension;
        return uuid;
    }

    public static String encryptEmail(String email) {
        String name = email.split("@")[0];
        int nameLen = name.length();
        String domain = email.split("@")[1];
        StringBuilder stringBuilder = new StringBuilder();

        switch (nameLen) {
            case 1:
            case 2:
                stringBuilder.append(name.charAt(0));
                break;
            default:
                stringBuilder.append(name.charAt(0) + "" + name.charAt(1));
        }
        stringBuilder.append("*****");
        return stringBuilder + "@" + domain;
    }

    public static String createRandomAlphaNumberString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String createRandomString(int length) {
        return RandomStringUtils.random(length);
    }
}
