package com.service.core.user.dto;

import com.service.core.user.domain.UserDomain;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCommentDto {
    private String userId;
    private String userPassword;
    private String userNickname;
    private Long blogId;

    public static UserCommentDto from(UserDomain userDomain) {
        if (userDomain == null) {
            return UserCommentDto.builder()
                    .userId(ConstUtil.UNDEFINED)
                    .userPassword(ConstUtil.UNDEFINED)
                    .userNickname(ConstUtil.UNDEFINED)
                    .blogId(Long.MAX_VALUE)
                    .build();
        } else {
            return UserCommentDto.builder()
                    .userId(userDomain.getUserId())
                    .userPassword(userDomain.getPassword())
                    .userNickname(userDomain.getNickname())
                    .blogId(userDomain.getBlog().getId())
                    .build();
        }
    }
}
