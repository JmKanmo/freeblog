package com.service.core.music.domain;


import com.service.core.blog.domain.Blog;
import com.service.core.music.model.UserMusicConfigInput;
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

    private int lrcType; // TODO lrc 데이터 구하는 것이 어려워 차후 방안 모색 ...

    private boolean autoPlay;

    private boolean duplicatePlay;

    @Enumerated(EnumType.STRING)
    private PlayOrder playOrder;

    @Enumerated(EnumType.STRING)
    private PlayMode playMode;

    @Enumerated(EnumType.STRING)
    private LoopMode loopMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;


    public static UserMusicConfig from(UserMusicConfigInput userMusicConfigInput, Blog blog) {
        return UserMusicConfig.builder()
                .listFolded(userMusicConfigInput.isListFolded())
                .listMaxHeight(userMusicConfigInput.getListMaxHeight())
                .autoPlay(userMusicConfigInput.isAutoPlay())
                .duplicatePlay(userMusicConfigInput.isDuplicatePlay())
                .playOrder(PlayOrder.of(userMusicConfigInput.getPlayOrder()))
                .playMode(PlayMode.of(userMusicConfigInput.getPlayMode()))
                .loopMode(LoopMode.of(userMusicConfigInput.getLoopMode()))
                .blog(blog)
                .build();
    }

    public void update(UserMusicConfigInput userMusicConfigInput) {
        setListFolded(userMusicConfigInput.isListFolded());
        setListMaxHeight(userMusicConfigInput.getListMaxHeight());
        setAutoPlay(userMusicConfigInput.isAutoPlay());
        setDuplicatePlay(userMusicConfigInput.isDuplicatePlay());
        setPlayOrder(PlayOrder.of(userMusicConfigInput.getPlayOrder()));
        setPlayMode(PlayMode.of(userMusicConfigInput.getPlayMode()));
        setLoopMode(LoopMode.of(userMusicConfigInput.getLoopMode()));
    }

    enum PlayOrder {
        RANDOM("RANDOM"),
        LIST("LIST"),
        NONE("NONE");

        private final String playOrder;

        PlayOrder(String playOrder) {
            this.playOrder = playOrder;
        }

        static PlayOrder of(String playOrder) {
            try {
                return PlayOrder.valueOf(playOrder.toUpperCase());
            } catch (Exception e) {
                return NONE;
            }
        }
    }

    enum PlayMode {
        FIXED("FIXED"),
        MINI("MINI"),
        NONE("NONE");

        private final String playMode;

        PlayMode(String playMode) {
            this.playMode = playMode;
        }

        static PlayMode of(String playMode) {
            try {
                return PlayMode.valueOf(playMode.toUpperCase());
            } catch (Exception e) {
                return NONE;
            }
        }
    }

    enum LoopMode {
        ALL("ALL"),
        ONE("ONE"),
        NONE("NONE");

        private final String loopMode;

        LoopMode(String loopMode) {
            this.loopMode = loopMode;
        }

        static LoopMode of(String loopMode) {
            try {
                return LoopMode.valueOf(loopMode.toUpperCase());
            } catch (Exception e) {
                return NONE;
            }
        }
    }
}
