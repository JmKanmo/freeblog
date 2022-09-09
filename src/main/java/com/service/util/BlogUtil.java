package com.service.util;

import com.amazonaws.services.s3.model.ObjectMetadata;
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
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class BlogUtil {
    public static String mappingRedirectUrl(String redirectUrl) {
        if (redirectUrl.endsWith("/user/email-auth")
                || redirectUrl.endsWith("/user/update/password")) {
            return "/";
        }
        return redirectUrl;
    }

    public static boolean checkBlogOwner(Principal principal, int emailHash) {
        if (principal == null || Objects.hashCode(principal.getName()) != emailHash) {
            return false;
        }
        return true;
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

    public static ObjectMetadata initObjectMetaData(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
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

    public static <T> List<T> convertArrayToList(T[] array) {
        List<T> list = new ArrayList<>();
        for (T item : array) {
            list.add(item);
        }
        return list;
    }

    public static <T> Stream<T> getSlice(Stream<T> stream, int fromIndex, int toIndex) {
        return stream
                // 건너뛸 요소의 총 개수 지정
                .skip(fromIndex)
                // 스트림이 제한되어야 하는 요소의 총 수를 지정합니다.
                .limit(toIndex - fromIndex + 1);
    }
}
