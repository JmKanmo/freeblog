package com.service.core.views.dto;

import com.service.core.views.domain.BlogVisitors;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogVisitorsDto {
    private final long todayVisitors;
    private final long yesterdayVisitors;
    private final long totalVisitors;

    public static BlogVisitorsDto from(BlogVisitors blogVisitors) {
        if (blogVisitors == null) {
            // 전부 0으로 표시
            return BlogVisitorsDto.builder().build();
        } else {
            return BlogVisitorsDto.builder()
                    .todayVisitors(blogVisitors.getTodayVisitors())
                    .yesterdayVisitors(blogVisitors.getYesterdayVisitors())
                    .totalVisitors(blogVisitors.getTotalVisitors())
                    .build();
        }
    }
}
