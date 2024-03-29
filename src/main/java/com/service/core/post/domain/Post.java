package com.service.core.post.domain;

import com.service.core.blog.domain.Blog;
import com.service.core.category.domain.Category;
import com.service.core.comment.domain.Comment;
import com.service.core.post.model.BlogPostInput;
import com.service.core.tag.domain.Tag;
import com.service.util.ConstUtil;
import com.service.util.domain.BaseTimeEntity;
import io.netty.util.internal.StringUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"category", "blog"})
@Table(name = "post", indexes = {
        @Index(name = "post_idx_seq", columnList = "seq")
})
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private Long seq;

    private String title;

    private String writer;

    private String thumbnailImage;

    private boolean isDelete;

    @Lob
    @Length(max = ConstUtil.MAX_POST_CONTENT_SIZE)
    private String contents;

    private String summary;

    private String metaKey;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "post")
    private List<Tag> tagList;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "post")
    private List<Comment> commentList;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    public static Post from(BlogPostInput blogPostInput) {
        return Post.builder()
                .title(blogPostInput.getTitle())
                .thumbnailImage(StringUtil.isNullOrEmpty(blogPostInput.getPostThumbnailImage()) ? ConstUtil.UNDEFINED : blogPostInput.getPostThumbnailImage())
                .contents(blogPostInput.getContents())
                .summary(blogPostInput.getSummary())
                .metaKey(blogPostInput.getMetaKey())
                .isBaseTimezone(true)
                .build();
    }
}
