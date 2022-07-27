package com.service.core.user.domain;

import com.service.util.ConstUtil;
import com.service.util.JmUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialAddress {
    private String address;
    private String github;
    private String twitter;
    private String instagram;

    public static SocialAddress from(SocialAddress socialAddress) {
        if (socialAddress == null) {
            return SocialAddress.builder()
                    .address(ConstUtil.UNDEFINED)
                    .github(ConstUtil.UNDEFINED)
                    .twitter(ConstUtil.UNDEFINED)
                    .instagram(ConstUtil.UNDEFINED)
                    .build();
        } else {
            return SocialAddress.builder()
                    .address(JmUtil.ofNull(socialAddress.getAddress()))
                    .github(JmUtil.ofNull(socialAddress.getGithub()))
                    .twitter(JmUtil.ofNull(socialAddress.getTwitter()))
                    .instagram(JmUtil.ofNull(socialAddress.getInstagram()))
                    .build();
        }
    }
}
