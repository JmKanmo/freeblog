package com.service.core.user.dto;

import com.service.core.user.domain.SocialAddress;
import com.service.core.user.domain.User;
import com.service.core.user.model.UserStatus;
import com.service.util.ConstUtil;
import com.service.util.JmUtil;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDto {
    private final String id;
    private final String email;
    private final String greetings;
    private final String intro;
    private final boolean isAuth;
    private final String profileImages;
    private final String status;
    private final SocialAddress socialAddress;

    public static UserDto fromEntity(User user) {
        if (user == null) {
            return UserDto.builder()
                    .id(ConstUtil.UNDEFINED)
                    .email(ConstUtil.UNDEFINED)
                    .greetings(ConstUtil.UNDEFINED)
                    .intro(ConstUtil.UNDEFINED)
                    .profileImages(ConstUtil.UNDEFINED)
                    .status(UserStatus.NOT_AUTH.name())
                    .socialAddress(SocialAddress.from(user.getSocialAddress()))
                    .build();
        } else {
            return UserDto.builder()
                    .id(JmUtil.ofNull(user.getUserId()))
                    .email(JmUtil.ofNull(user.getEmail()))
                    .greetings(JmUtil.ofNull(user.getGreetings()))
                    .intro(JmUtil.ofNull(user.getIntro()))
                    .isAuth(user.isAuth())
                    .profileImages(JmUtil.ofNull(user.getProfileImage()))
                    .status(JmUtil.ofNull(user.getStatus().name()))
                    .socialAddress(SocialAddress.from(user.getSocialAddress()))
                    .build();
        }
    }
}
