package com.service.core.notice.domain;

import com.service.util.ConstUtil;
import com.service.util.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    private String title;

    @Lob
    @Length(max = ConstUtil.MAX_NOTICE_CONTENT_SIZE)
    private String contents;

    private String summary;

    private String uploadKey;
}

