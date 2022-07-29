package com.service.core.user.domain;


import com.service.core.blog.domain.Blog;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.model.UserStatus;
import com.service.util.BaseTimeEntity;
import com.service.util.JmUtil;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "blog")
public class User extends BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    private String password;

    private String email;

    private String nickname;

    private String greetings;

    @Lob
    private String intro;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Embedded
    private SocialAddress socialAddress;

    /**
     * 추후에 Redis 도입 후, Redis에서 관리 하도록
     */
    private boolean isAuth;
    private String authKey;
    private LocalDateTime authExpireDateTime;

    private String updatePasswordKey;
    private LocalDateTime updatePasswordExpireDateTime;

    public static User from(UserSignUpInput userSignUpInput) {
        return User.builder()
                .userId(userSignUpInput.getId())
                .email(userSignUpInput.getEmail())
                .password(BCrypt.hashpw(userSignUpInput.getPassword(), BCrypt.gensalt()))
                .nickname(userSignUpInput.getNickname())
                .greetings(userSignUpInput.getGreetings())
                .authKey(JmUtil.createRandomAlphaNumberString(20))
                .isAuth(false)
                .authExpireDateTime(LocalDateTime.now())
                .status(UserStatus.NOT_AUTH)
                .socialAddress(SocialAddress.builder()
                        .address(userSignUpInput.getAddress())
                        .github(userSignUpInput.getGithub())
                        .twitter(userSignUpInput.getTwitter())
                        .instagram(userSignUpInput.getInstagram())
                        .build())
                .build();
    }
}
