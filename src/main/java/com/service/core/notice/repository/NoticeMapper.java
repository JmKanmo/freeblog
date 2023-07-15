package com.service.core.notice.repository;

import com.service.core.notice.dto.NoticeDetailDto;
import com.service.core.notice.dto.NoticeDto;
import com.service.core.notice.paging.NoticeSearchPagingDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
    int findNoticeCount();

    int findNoticeCountByKeyword(String searchType, String keyword);

    List<NoticeDto> findNoticeDtoListByPaging(@Param("noticeSearchPagingDto") NoticeSearchPagingDto noticeSearchPagingDto);

    NoticeDetailDto findNoticeDetailDtoById(Long noticeId);
}
