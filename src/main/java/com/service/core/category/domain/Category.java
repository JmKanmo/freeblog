package com.service.core.category.domain;

import com.service.core.blog.domain.Blog;
import com.service.core.category.model.CategoryInput;
import com.service.core.post.domain.Post;
import com.service.util.domain.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "blog")
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private Long parentId;

    @Column(length = 25)
    private String name;

    private Long seq;

    private boolean isDelete;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "category")
    private List<Post> postList;

    public static Category from(Blog blog) {
        return Category.builder()
                .parentId(0L)
                .name("게시판")
                .seq(1L)
                .blog(blog)
                .build();
    }

    public static Category from(CategoryInput categoryInput, Blog blog) {
        return Category.builder()
                .parentId(categoryInput.getParentId())
                .name(categoryInput.getName())
                .seq(categoryInput.getSeq())
                .blog(blog)
                .isDelete(false)
                .build();
    }
}
