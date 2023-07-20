package com.service.core.notice.service;

import com.service.core.notice.domain.NoticeAlarm;
import com.service.core.notice.dto.NoticeDto;
import com.service.core.notice.paging.NoticePaginationResponse;
import com.service.core.notice.paging.NoticeSearchPagingDto;

import java.util.List;

public interface NoticeService {
    NoticePaginationResponse<List<NoticeDto>> findNoticeByKeyword(NoticeSearchPagingDto noticeSearchPagingDto);

    boolean checkNoticeAlarm();

    NoticeAlarm findNoticeAlarmById(Integer id);

    List<NoticeAlarm> findNoticeAlarms();

    NoticeAlarm findRecentNoticeAlarm();
}
