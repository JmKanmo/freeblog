package com.service.core.music.domain;

import com.service.util.domain.BaseTimeEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "music")
@ToString(exclude = "musicCategory")
public class Music extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long id;

    private String name;

    private String artist;

    private String url;

    private String cover;

    private String lrc; // TODO lrc 데이터 구하는 것이 어려워 차후 방안 모색 ...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_category_id")
    private MusicCategory musicCategory;
}