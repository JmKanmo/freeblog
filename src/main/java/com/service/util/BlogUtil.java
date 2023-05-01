package com.service.util;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogServiceException;
import com.service.core.error.model.UserAuthException;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.model.UserStatus;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.exceptions.TemplateEngineException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static boolean checkEmptyOrUndefinedStr(String str) {
        return str == null || str.isEmpty() || str.equals("") ? true : false;
    }

    public static String checkAndGetRepText(String text, String refText) {
        return checkEmptyOrUndefinedStr(text) ? refText : text;
    }

    public static String getCurrentIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (IOException ioException) {
            return ConstUtil.UNDEFINED;
        }
    }

    public static boolean checkBlogOwner(Principal principal, int emailHash) {
        if ((principal == null || principal.getName() == null) || Objects.hashCode(principal.getName()) != emailHash) {
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

    public static String getImageFileUUIDBySftp(MultipartFile multipartFile) {
        String fileName = multipartFile.getContentType();
        String extension = getImageException(fileName.substring(fileName.lastIndexOf("/") + 1));
        String uuid = UUID.randomUUID() + "." + extension;
        return uuid;
    }

    public static String getImageException(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "png";
        }

        switch (extension) {
            case "jpeg":
                return "jpg";
            case "GIF":
                return "gif";
            default:
                return extension;
        }
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

    public static String formatLocalDateTimeToStrByPattern(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime != null ? localDateTime.format(formatter) : "";
    }

    public static String formatLocalDateTimeToStr(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime != null ? localDateTime.format(formatter) : "";
    }

    public static LocalDateTime nowByZoneId() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"));
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

    public static String currentRequestUrl() {
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        StringBuilder requestURL = new StringBuilder(httpServletRequest.getRequestURL().toString());
        String queryString = httpServletRequest.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    public static boolean checkFieldValidation(String field, int len) {
        if (field == null || field.isEmpty() || field.isBlank() || StringUtils.containsWhitespace(field) || field.length() > len) {
            return false;
        }
        return true;
    }

    public static boolean parseAndGetCheckBox(String checked) {
        if (checked == null) {
            return false;
        } else if (checked.equals("on")) {
            return true;
        }
        return false;
    }

    public static String getErrorMessage(Exception exception) {
        if (exception instanceof BlogServiceException) {
            return exception.getMessage();
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return "세션이 만료되어 작업에 실패하였습니다.";
        } else if (exception instanceof DataAccessException) {
            return "데이터베이스 쿼리 수행에 실패하였습니다.";
        }
        return ConstUtil.UNDEFINED_ERROR;
    }

    public static String createKeywordByText(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String parse : text.split(" ")) {
            stringBuilder.append(parse + "* ");
        }
        return stringBuilder.toString();
    }

    public static int hashCode(Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder.append(arg);
        }
        return stringBuilder.toString().hashCode();
    }

    public static int getClientAccessId(HttpServletRequest httpServletRequest, Principal principal) {
        String ip = getClientIp(httpServletRequest);
        String url = httpServletRequest.getHeader("User-Agent");

        if (principal == null || principal.getName() == null) {
            return (ip + url + "not-logged-in").hashCode();
        } else {
            return (ip + url + "logged-in_" + principal.getName()).hashCode();
        }
    }

    public static String getClientIp(HttpServletRequest httpServletRequest) {
        return (null != httpServletRequest.getHeader("X-FORWARDED-FOR")) ?
                httpServletRequest.getHeader("X-FORWARDED-FOR") : httpServletRequest.getRemoteAddr();
    }

    public static String formatNumberComma(long number) {
        return String.valueOf(number).replaceAll("\\B(?=(\\d{3})+(?!\\d))", ",");
    }
}
