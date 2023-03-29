package com.service.core.post.domain;

import com.service.core.post.dto.PostDetailDto;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostView {
    private static final long serialVersionUID = ConstUtil.SERIAL_VERSION_ID;
    private Long postId;
    private Long view;

    public static PostView from(PostDetailDto postDetailDto) {
        return PostView.builder()
                .postId(postDetailDto.getId())
                .build();
    }
}
