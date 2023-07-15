package com.service.core.notice.repository;

import com.service.core.notice.domain.NoticeAlarm;
import org.springframework.data.repository.CrudRepository;

public interface NoticeAlarmRepository extends CrudRepository<NoticeAlarm, Integer> {
}
