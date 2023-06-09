package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PostOverviewDto {
    private final Long id;
    private final String registerTime;
    private final String updateTime;
}
