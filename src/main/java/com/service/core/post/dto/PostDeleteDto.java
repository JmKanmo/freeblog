package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDeleteDto {
    private final Long id;
    private Long seq;
    private boolean isDelete;
    private String title;
}
