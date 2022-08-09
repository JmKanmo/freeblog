package com.service.core.user.dto;

import com.service.core.user.domain.UserDomain;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserBasicDto {
    private final String id;
    private final String profileImages;

    public static UserBasicDto fromEntity(UserDomain user) {
        if (user == null) {
            return UserBasicDto.builder()
                    .id(ConstUtil.UNDEFINED)
                    .profileImages(ConstUtil.UNDEFINED)
                    .build();
        } else {
            return UserBasicDto.builder()
                    .id(BlogUtil.ofNull(user.getUserId()))
                    .profileImages(BlogUtil.ofNull(user.getProfileImage()))
                    .build();
        }
    }
}
