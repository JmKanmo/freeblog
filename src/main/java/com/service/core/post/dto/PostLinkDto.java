package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLinkDto {
    private final String link;
    private final String title;
    private final Integer seq;
}
