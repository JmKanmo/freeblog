package com.service.core.post.service;

import com.service.core.post.dto.PostTotalDto;


public interface PostService {
    PostTotalDto findTotalPost(Long blogId, String type);
}
