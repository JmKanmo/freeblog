package com.service.core.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UserProfileDto {
    private String id;
    private String nickname;
    @JsonIgnore
    private int emailHash;
    private String greetings;
    private String profileImages;
    // social address
    private String address;
    private String github;
    private String twitter;
    private String instagram;

    public static UserProfileDto fromEntity(UserDomain user) {
        if (user == null) {
            return UserProfileDto.builder()
                    .id(ConstUtil.UNDEFINED)
                    .nickname(ConstUtil.UNDEFINED)
                    .greetings(ConstUtil.UNDEFINED)
                    .profileImages(ConstUtil.UNDEFINED)
                    .emailHash(new Random().nextInt())
                    .address(ConstUtil.UNDEFINED)
                    .github(ConstUtil.UNDEFINED)
                    .twitter(ConstUtil.UNDEFINED)
                    .instagram(ConstUtil.UNDEFINED)
                    .build();
        } else {
            return UserProfileDto.builder()
                    .id(BlogUtil.ofNull(user.getUserId()))
                    .emailHash(user.getEmail().hashCode())
                    .nickname(BlogUtil.ofNull(user.getNickname()))
                    .greetings(BlogUtil.ofNull(user.getGreetings()))
                    .profileImages(BlogUtil.ofNull(user.getProfileImage()))
                    .address(user.getSocialAddress().getAddress())
                    .github(user.getSocialAddress().getGithub())
                    .twitter(user.getSocialAddress().getTwitter())
                    .instagram(user.getSocialAddress().getInstagram())
                    .build();
        }
    }

    public static UserProfileDto from(UserProfileMapperDto userProfileMapperDto) {
        return UserProfileDto.builder()
                .id(BlogUtil.ofNull(userProfileMapperDto.getId()))
                .emailHash(userProfileMapperDto.getEmail().hashCode())
                .nickname(BlogUtil.ofNull(userProfileMapperDto.getNickname()))
                .greetings(BlogUtil.ofNull(userProfileMapperDto.getGreetings()))
                .profileImages(BlogUtil.ofNull(userProfileMapperDto.getProfileImages()))
                .build();
    }
}
