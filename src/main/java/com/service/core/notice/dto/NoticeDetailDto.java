package com.service.core.notice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeDetailDto {
    private Long noticeId;
    private final String title;
    private final String contents;
    private String summary;
    private String uploadKey;
    private final String registerTime;
    private final String updateTime;
    private final Boolean isBaseTimezone;
}
