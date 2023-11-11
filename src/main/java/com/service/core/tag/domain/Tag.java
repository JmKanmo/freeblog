package com.service.core.tag.domain;

import com.service.core.blog.domain.Blog;
import com.service.core.post.domain.Post;
import com.service.util.domain.BaseTimeEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "post")
public class Tag extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static Tag from(Post post, String tagName) {
        return Tag.builder()
                .name(tagName)
                .post(post)
                .isBaseTimezone(true)
                .build();
    }
}
