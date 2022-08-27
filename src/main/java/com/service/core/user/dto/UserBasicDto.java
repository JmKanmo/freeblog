package com.service.core.user.dto;

import com.service.core.user.domain.UserDomain;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDto {
    private String id;
    private String nickname;

    private int emailHash;
    private String greetings;
    private String profileImages;


    public static UserBasicDto fromEntity(UserDomain user) {
        if (user == null) {
            return UserBasicDto.builder()
                    .id(ConstUtil.UNDEFINED)
                    .nickname(ConstUtil.UNDEFINED)
                    .greetings(ConstUtil.UNDEFINED)
                    .profileImages(ConstUtil.UNDEFINED)
                    .emailHash(new Random().nextInt())
                    .build();
        } else {
            return UserBasicDto.builder()
                    .id(BlogUtil.ofNull(user.getUserId()))
                    .emailHash(user.getEmail().hashCode())
                    .nickname(BlogUtil.ofNull(user.getNickname()))
                    .greetings(BlogUtil.ofNull(user.getGreetings()))
                    .profileImages(BlogUtil.ofNull(user.getProfileImage()))
                    .build();
        }
    }
}
