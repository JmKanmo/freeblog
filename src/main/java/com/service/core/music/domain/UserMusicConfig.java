package com.service.core.music.domain;


import com.service.core.blog.domain.Blog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_music_config")
public class UserMusicConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_music_config_id")
    private Long id;

    private boolean listFolded;

    private int listMaxHeight;

    private int lrcType;

    private boolean autoPlay;

    private boolean mutex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;
}
