package com.service.core.user.domain;


import com.service.core.blog.domain.Blog;
import com.service.core.user.model.UserSignUpInput;
import com.service.core.user.model.UserStatus;
import com.service.util.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    private String userId;

    private String password;

    private String email;

    private String greetings;

    @Lob
    private String intro;

    private boolean isAuth;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Embedded
    private SocialAddress socialAddress;

    public static User from(UserSignUpInput userSignUpInput) {
        return User.builder()
                .userId(userSignUpInput.getId())
                .email(userSignUpInput.getEmail())
                .password(BCrypt.hashpw(userSignUpInput.getPassword(), BCrypt.gensalt()))
                .greetings(userSignUpInput.getGreetings())
                .isAuth(false)
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
