package com.service.core.views.domain;

import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostView implements Serializable {
    private static final long serialVersionUID = ConstUtil.SERIAL_VERSION_ID;
    private Long postId;
    private Long blogId;
    private Long view;

    public static PostView from(Long blogId, Long postId, long view) {
        return PostView.builder()
                .blogId(blogId)
                .postId(postId)
                .view(view)
                .build();
    }

    public void incrementView() {
        this.view++;
    }
}
