package com.service.core.tag.dto;

import com.service.core.tag.domain.Tag;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TagDto {
    private final String name;

    public static TagDto fromEntity(Tag tag) {
        return TagDto.builder()
                .name(tag.getName())
                .build();
    }
}
