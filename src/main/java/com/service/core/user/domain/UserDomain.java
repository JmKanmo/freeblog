package com.service.core.user.domain;


import com.service.core.blog.domain.Blog;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.model.UserStatus;
import com.service.util.domain.BaseTimeEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "blog")
@Table(name = "user", indexes = {
        @Index(name = "user_idx_email", columnList = "email"),
        @Index(name = "user_idx_nickname", columnList = "nickname")
})
public class UserDomain extends BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    private String password;

    private String email;

    private String nickname;

    private String greetings;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Embedded
    private SocialAddress socialAddress;

    private boolean isAuth;

    private String metaKey;

    private LocalDateTime emailAuthTime;
    private LocalDateTime passwordUpdateTime;
    private LocalDateTime withdrawTime;

    public static UserDomain from(UserSignUpInput userSignUpInput) {
        return UserDomain.builder()
                .userId(userSignUpInput.getId())
                .email(userSignUpInput.getEmail())
                .password(BCrypt.hashpw(userSignUpInput.getPassword(), BCrypt.gensalt()))
                .nickname(userSignUpInput.getNickname())
                .greetings(userSignUpInput.getGreetings())
                .isAuth(false)
                .status(UserStatus.NOT_AUTH)
                .socialAddress(SocialAddress.builder()
                        .address(userSignUpInput.getAddress())
                        .github(userSignUpInput.getGithub())
                        .twitter(userSignUpInput.getTwitter())
                        .instagram(userSignUpInput.getInstagram())
                        .build())
                .isBaseTimezone(true)
                .build();
    }
}
