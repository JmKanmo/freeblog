package com.service.core.notice;

import com.service.core.notice.domain.NoticeAlarm;
import com.service.core.notice.dto.NoticeDto;
import com.service.core.notice.paging.NoticeSearchPagingDto;
import com.service.core.notice.repository.NoticeAlarmRepository;
import com.service.core.notice.service.NoticeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class NoticeAlarmTest {
    @Autowired
    private NoticeAlarmRepository noticeAlarmRepository;

    @Autowired
    private NoticeService noticeService;

    @Test
    public void noticeKeywordSearchTest() {
        Object obj = noticeService.findNoticeByKeyword(new NoticeSearchPagingDto());
        Assertions.assertNotNull(obj);
    }

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
