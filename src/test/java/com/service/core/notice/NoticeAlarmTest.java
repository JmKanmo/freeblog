package com.service.core.notice;

import com.service.core.notice.domain.NoticeAlarm;
import com.service.core.notice.repository.NoticeAlarmRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NoticeAlarmTest {
    @Autowired
    private NoticeAlarmRepository noticeAlarmRepository;

    @Test
    public void noticeAlarmFindTest() {
        Iterable<NoticeAlarm> noticeAlarmList = noticeAlarmRepository.findAll();

        noticeAlarmList.forEach(noticeAlarm -> {
            System.out.println(noticeAlarm);
        });
    }

    @Test
    public void findNoticeAlarm() {
        Integer id = -643429334;
        Assertions.assertNotNull(noticeAlarmRepository.findById(id));
    }
}
