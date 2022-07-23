package com.service.core.post.domain;

import com.service.core.blog.domain.Blog;
import com.service.core.category.domain.Category;
import com.service.core.tag.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private String id;

    private String title;

    private String writer;

    @Lob
    private String contents;

    private LocalDateTime registerTime;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "post")
    private List<Tag> tagList;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;
}
