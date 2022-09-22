package com.service.config.redis;

import java.io.Serializable;

public class LikePost implements Serializable {
    private String title;
    private String category;

    public LikePost(String title, String category) {
        this.title = title;
        this.category = category;
    }

    @Override
    public String toString() {
        return "LikePost{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
