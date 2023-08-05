package com.service.core.notice.service.impl;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.NoticeManageException;
import com.service.core.notice.domain.NoticeAlarm;
import com.service.core.notice.dto.NoticeDetailDto;
import com.service.core.notice.dto.NoticeDto;
import com.service.core.notice.paging.NoticePagination;
import com.service.core.notice.paging.NoticePaginationResponse;
import com.service.core.notice.paging.NoticeSearchPagingDto;
import com.service.core.notice.repository.NoticeAlarmRepository;
import com.service.core.notice.repository.NoticeMapper;
import com.service.core.notice.repository.NoticeRepository;
import com.service.core.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private final NoticeAlarmRepository noticeAlarmRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;

    @Override
    public NoticePaginationResponse<List<NoticeDto>> findNoticeByKeyword(NoticeSearchPagingDto noticeSearchPagingDto) {
        int noticeCount = noticeMapper.findNoticeCountByKeyword(noticeSearchPagingDto.getSearchType(), noticeSearchPagingDto.getKeyword());
        NoticePagination noticePagination = new NoticePagination(noticeCount, noticeSearchPagingDto);
        noticeSearchPagingDto.setNoticePagination(noticePagination);
        return new NoticePaginationResponse<>(noticeMapper.findNoticeDtoListByPaging(noticeSearchPagingDto), noticePagination);
    }

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

    @Override
    public NoticeDetailDto findNoticeDetailDtoById(Long noticeId) {
        return noticeMapper.findNoticeDetailDtoById(noticeId);
    }
}
