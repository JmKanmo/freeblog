package com.service.core.music.domain;

import com.service.core.blog.domain.Blog;
import com.service.core.music.model.UserMusicInput;
import com.service.util.BlogUtil;
import com.service.util.domain.BaseTimeEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_music",
        indexes = {
                @Index(name = "hashcode_idx_id", columnList = "hashCode")
        })
@ToString(exclude = "userMusicCategory")
public class UserMusic extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_music_id")
    private Long id;

    private String name;

    private String artist;

    private String url;

    private String cover;

    private String lrc; // TODO lrc 데이터 구하는 것이 어려워 차후 방안 모색 ...

    private boolean isDelete;

    private int hashCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_category_id")
    private UserMusicCategory userMusicCategory;

    public static UserMusic from(String email, Blog blog, UserMusicInput userMusicInput, UserMusicCategory userMusicCategory) {
        return UserMusic.builder()
                .name(userMusicInput.getTitle())
                .artist(userMusicInput.getArtist())
                .url(userMusicInput.getUrl())
                .cover(userMusicInput.getCover())
                .lrc(userMusicInput.getLrc())
                .hashCode(BlogUtil.getHashCode(
                        email,
                        blog.getId(),
                        userMusicInput.getMusicId(),
                        userMusicInput.getMusicCategoryId(),
                        userMusicInput.getTitle(),
                        userMusicInput.getArtist(),
                        userMusicInput.getUrl(),
                        userMusicInput.getCover(),
                        userMusicInput.getLrc()))
                .userMusicCategory(userMusicCategory)
                .isBaseTimezone(true)
                .build();
    }
}
