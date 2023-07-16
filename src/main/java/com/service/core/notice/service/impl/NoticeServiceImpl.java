package com.service.core.notice.service.impl;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.NoticeManageException;
import com.service.core.notice.domain.NoticeAlarm;
import com.service.core.notice.repository.NoticeAlarmRepository;
import com.service.core.notice.repository.NoticeRepository;
import com.service.core.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
            NoticeAlarm noticeAlarm = noticeAlarmIterator.next();

            if (noticeAlarm != null && noticeAlarm.getCreatedTime() != null) {
                noticeAlarmList.add(noticeAlarm);
            }
        }
        return noticeAlarmList;
    }

    @Override
    public NoticeAlarm findRecentNoticeAlarm() {
        List<NoticeAlarm> noticeAlarmList = findNoticeAlarms();

        if (noticeAlarmList.isEmpty()) {
            return null;
        } else {
            Collections.sort(noticeAlarmList, new Comparator<NoticeAlarm>() {
                @Override
                public int compare(NoticeAlarm o1, NoticeAlarm o2) {
                    return Long.compare(o2.getCreatedTime(), o1.getCreatedTime());
                }
            });
            return noticeAlarmList.get(0);
        }
    }
}
