package com.service.core.blog.domain;

import com.service.core.category.domain.Category;
import com.service.core.music.domain.UserMusicCategory;
import com.service.core.post.domain.Post;
import com.service.core.user.domain.UserDomain;
import com.service.core.user.model.UserSignUpInput;
import com.service.util.ConstUtil;
import com.service.util.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Blog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;

    private String name;

    private boolean isDelete;

    @Lob
    @Length(max = ConstUtil.MAX_INTRO_CONTENT_SIZE)
    private String intro;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "blog")
    private UserDomain user;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "blog")
    private List<Category> categoryList;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "blog")
    private List<Post> postList;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "blog")
    private List<UserMusicCategory> userMusicCategoryList;

    public static Blog from(UserSignUpInput userSignUpInput) {
        return Blog.builder()
                .name(userSignUpInput.getBlogName())
                .intro(ConstUtil.DEFAULT_USER_INTRO)
                .isBaseTimezone(true)
                .build();
    }
}
