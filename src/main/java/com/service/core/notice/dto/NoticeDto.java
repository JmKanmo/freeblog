package com.service.core.notice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeDto {
    private final long noticeId;
    private final String registerTime;
    private final String title;
    private final String summary;
    private final String contents;
}

