package com.service.core.tag.service;

import com.service.core.post.domain.Post;

import java.util.List;

public interface TagService {
    void register(List<String> tagStrList, Post post);
}
