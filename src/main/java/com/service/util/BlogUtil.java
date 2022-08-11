package com.service.util;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.model.UserStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class BlogUtil {
    public static String mappingRedirectUrl(String redirectUrl) {
        if (redirectUrl.endsWith("/user/email-auth")
                || redirectUrl.endsWith("/user/update/password")) {
            return "/";
        }
        return redirectUrl;
    }

    public static boolean isAuth(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    public static String redirect(String url) {
        return String.format("redirect:%s", url);
    }

    public static String getLoginFailMessage(AuthenticationException exception) {
        if (exception instanceof AuthenticationServiceException) {
            return exception.getMessage();

        } else if (exception instanceof BadCredentialsException) {
            return ServiceExceptionMessage.ID_PW_WRONG.message();

        } else if (exception instanceof LockedException) {
            return ServiceExceptionMessage.LOCK_ACCOUNT.message();

        } else if (exception instanceof DisabledException) {
            return ServiceExceptionMessage.DEACTIVATE_ACCOUNT.message();

        } else if (exception instanceof AccountExpiredException) {
            return ServiceExceptionMessage.EXPIRED_ACCOUNT.message();

        } else if (exception instanceof CredentialsExpiredException) {
            return ServiceExceptionMessage.EXPIRED_PASSWORD.message();
        }
        return exception.getMessage();
    }

    public static boolean checkUserStatus(UserStatus userStatus) {
        switch (userStatus) {
            case WITHDRAW:
                throw new UserAuthException(ServiceExceptionMessage.WITHDRAW_ACCOUNT);

            case STOP:
                throw new UserAuthException(ServiceExceptionMessage.STOP_ACCOUNT);
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

    public static Integer createUserAuthId(UserDomain userDomain) {
        return Objects.hashCode(userDomain.getEmail() + ":" + userDomain.getUserId());
    }

    public static String formatLocalDateTimeToStr(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return localDateTime != null ? localDateTime.format(formatter) : "";
    }
}
