package com.service.core.user.dto;

import com.service.core.user.domain.UserDomain;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserHeaderDto implements Serializable {
    private static final long serialVersionUID = ConstUtil.SERIAL_VERSION_ID;
    private String id;
    private String nickname;
    private String profileImages;


    public static UserHeaderDto fromEntity(UserDomain user) {
        if (user == null) {
            return UserHeaderDto.builder()
                    .id(ConstUtil.UNDEFINED)
                    .nickname(ConstUtil.UNDEFINED)
                    .profileImages(ConstUtil.UNDEFINED)
                    .build();
        } else {
            return UserHeaderDto.builder()
                    .id(BlogUtil.ofNull(user.getUserId()))
                    .nickname(BlogUtil.ofNull(user.getNickname()))
                    .profileImages(BlogUtil.ofNull(user.getProfileImage()))
                    .build();
        }
    }
}
