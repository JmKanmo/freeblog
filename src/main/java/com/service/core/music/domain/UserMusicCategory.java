package com.service.core.music.domain;

import com.service.core.blog.domain.Blog;
import com.service.core.tag.domain.Tag;
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
@Table(name = "user_music_category")
public class UserMusicCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_music_category_id")
    private Long id;

    private String name;

    private boolean isDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "userMusicCategory")
    private List<UserMusic> userMusicList;
}
