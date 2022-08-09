package com.service.core.user.dto;

import com.service.core.user.domain.SocialAddress;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.model.UserStatus;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserSettingDto {
    private final String email;
    private final String id;
    private final String nickname;
    private final String greetings;
    private final String blogName;
    private final String intro;
    private final boolean isAuth;
    private final String profileImages;
    private final String status;
    private final SocialAddress socialAddress;
    private final String registerTime;
    private final String passwordUpdateTime;

    public static UserSettingDto fromEntity(UserDomain user) {
        if (user == null) {
            return UserSettingDto.builder()
                    .email(ConstUtil.UNDEFINED)
                    .id(ConstUtil.UNDEFINED)
                    .nickname(ConstUtil.UNDEFINED)
                    .greetings(ConstUtil.UNDEFINED)
                    .blogName(ConstUtil.UNDEFINED)
                    .intro(ConstUtil.UNDEFINED)
                    .profileImages(ConstUtil.UNDEFINED)
                    .status(UserStatus.NOT_AUTH.name())
                    .socialAddress(SocialAddress.from(user == null ? null : user.getSocialAddress()))
                    .registerTime(BlogUtil.formatLocalDateTimeToStr(LocalDateTime.now()))
                    .passwordUpdateTime(BlogUtil.formatLocalDateTimeToStr(LocalDateTime.now()))
                    .build();
        } else {
            return UserSettingDto.builder()
                    .email(BlogUtil.ofNull(user.getEmail()))
                    .id(BlogUtil.ofNull(user.getUserId()))
                    .nickname(BlogUtil.ofNull(user.getNickname()))
                    .greetings(BlogUtil.ofNull(user.getGreetings()))
                    .blogName(BlogUtil.ofNull(user.getBlog() == null ? null : user.getBlog().getName()))
                    .intro(BlogUtil.ofNull(user.getBlog() == null ? null : user.getBlog().getIntro()))
                    .isAuth(user.isAuth())
                    .profileImages(BlogUtil.ofNull(user.getProfileImage()))
                    .status(BlogUtil.ofNull(user.getStatus() == null ? UserStatus.NOT_AUTH.name() : user.getStatus().name()))
                    .socialAddress(SocialAddress.from(user.getSocialAddress()))
                    .registerTime(BlogUtil.formatLocalDateTimeToStr(user.getRegisterTime()))
                    .passwordUpdateTime(BlogUtil.formatLocalDateTimeToStr(user.getPasswordUpdateTime()))
                    .build();
        }
    }
}
