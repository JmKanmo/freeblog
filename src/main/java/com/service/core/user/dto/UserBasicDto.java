package com.service.core.user.dto;

import com.service.core.user.domain.UserDomain;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDto {
    private String id;
    private String profileImages;

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
