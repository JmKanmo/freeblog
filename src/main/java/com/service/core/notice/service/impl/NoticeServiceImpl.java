package com.service.core.notice.service.impl;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.NoticeManageException;
import com.service.core.notice.domain.NoticeAlarm;
import com.service.core.notice.repository.NoticeAlarmRepository;
import com.service.core.notice.repository.NoticeRepository;
import com.service.core.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeAlarmRepository noticeAlarmRepository;
    private final NoticeRepository noticeRepository;

    public boolean checkNoticeAlarm() {
        return noticeAlarmRepository.count() > 0;
    }

    public NoticeAlarm findNoticeAlarmById(Integer id) {
        return noticeAlarmRepository.findById(id).orElseThrow(() -> new NoticeManageException(ServiceExceptionMessage.NOTICE_NOT_FOUND));
    }

    public List<NoticeAlarm> findNoticeAlarms() {
        Iterator<NoticeAlarm> noticeAlarmIterator = noticeAlarmRepository.findAll().iterator();
        List<NoticeAlarm> noticeAlarmList = new ArrayList<>();

        while (noticeAlarmIterator.hasNext()) {
            noticeAlarmList.add(noticeAlarmIterator.next());
        }
        return noticeAlarmList;
    }

    @Override
    public NoticeAlarm findRecentNoticeAlarm() {
        List<NoticeAlarm> noticeAlarmList = findNoticeAlarms();

        if (noticeAlarmList.isEmpty()) {
            return null;
        } else {
            return noticeAlarmList.get(noticeAlarmList.size() - 1);
        }
    }
}
