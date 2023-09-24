package com.service.core.music.domain;

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
@Table(name = "music_category")
public class MusicCategory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_category_id")
    private Long id;

    private String name;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "musicCategory")
    private List<Music> musicList;
}
