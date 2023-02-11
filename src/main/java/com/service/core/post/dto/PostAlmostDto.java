package com.service.core.post.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostAlmostDto {
    private PostLinkDto prevPostLinkDto;
    private PostLinkDto nextPostLinkDto;

    public static PostAlmostDto from(Long seq, List<PostLinkDto> postLinkDtoList) {
        PostAlmostDto postAlmostDto = PostAlmostDto.builder().build();

        for (PostLinkDto postLinkDto : postLinkDtoList) {
            if (postLinkDto.getSeq() == seq - 1) {
                postAlmostDto.setPrevPostLinkDto(postLinkDto);
            } else if (postLinkDto.getSeq() == seq + 1) {
                postAlmostDto.setNextPostLinkDto(postLinkDto);
            }
        }
        return postAlmostDto;
    }
}
