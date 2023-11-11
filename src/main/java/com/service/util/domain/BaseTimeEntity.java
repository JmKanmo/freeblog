package com.service.util.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
    /**
     * registerTime, updateTime = 서버 기본 시간(UTC,GMT) 지정 여부 플래그
     * TRUE: 서버 기본 시간(UTC,GMT) 기준의 시간 데이터 저장
     * FALSE: (Asia/Seoul 타임존) 기준의 시간 데이터 저장 (기존에 저장 및 관리 된 데이터 호환)
     */
    private Boolean isBaseTimezone;

    @CreatedDate
    private LocalDateTime registerTime;

    @LastModifiedDate
    private LocalDateTime updateTime;
}
