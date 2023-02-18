package com.service.core.tag.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BlogTagDto {
    private final int tagCount;
    private final List<TagDto> tagDtoList;

    public static BlogTagDto from(List<TagDto> tagDtoList) {
        return BlogTagDto.builder()
                .tagCount(tagDtoList.size())
                .tagDtoList(tagDtoList)
                .build();
    }
}
