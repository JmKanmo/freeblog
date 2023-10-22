package com.service.core.music.domain;

import com.service.core.blog.domain.Blog;
import com.service.core.tag.domain.Tag;
import com.service.util.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "user_music_category", indexes = {
        @Index(name = "target_idx_id", columnList = "targetId")
})
public class UserMusicCategory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_music_category_id")
    private Long id;

    private Long targetId;

    private String name;

    private boolean isDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "userMusicCategory")
    private List<UserMusic> userMusicList;

    public static UserMusicCategory from(Long targetId, String name, Blog blog) {
        return UserMusicCategory.builder()
                .targetId(targetId)
                .name(name)
                .isDelete(false)
                .blog(blog)
                .build();
    }
}
