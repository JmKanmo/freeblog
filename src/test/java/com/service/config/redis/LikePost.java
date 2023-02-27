package com.service.config.redis;

import com.service.core.like.model.LikePostInput;
import com.service.util.ConstUtil;

import java.io.Serializable;

public class LikePost implements Serializable {
    private static final long serialVersionUID = ConstUtil.SERIAL_VERSION_ID;
    private Long postId;
    private String title;
    private String thumbnailImage;

    public LikePost(Long postId, String title, String thumbnailImage) {
        this.postId = postId;
        this.title = title;
        this.thumbnailImage = thumbnailImage;
    }

    @Override
    public String toString() {
        return "LikePost{" +
                "postId=" + postId +
                ", title='" + title + '\'' +
                ", thumbnailImage='" + thumbnailImage + '\'' +
                '}';
    }
}
