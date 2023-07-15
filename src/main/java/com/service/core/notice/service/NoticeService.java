package com.service.core.notice.service;

import com.service.core.notice.domain.NoticeAlarm;

import java.util.List;

public interface NoticeService {
    boolean checkNoticeAlarm();

    NoticeAlarm findNoticeAlarmById(Integer id);

    List<NoticeAlarm> findNoticeAlarms();

    NoticeAlarm findRecentNoticeAlarm();
}
