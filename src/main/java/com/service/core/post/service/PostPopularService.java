package com.service.core.post.service;

import com.service.core.post.dto.PostCardDto;
import com.service.util.redis.service.popular.PostPopularTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostPopularService {
    private final PostPopularTemplateService postPopularTemplateService;
    private final PostService postService;

    public List<PostCardDto> findPopularPost(long blogId) throws Exception {
        List<Long> popularPostIds = postPopularTemplateService.getPopularPost(blogId);
        return popularPostIds.stream().map(popularId -> PostCardDto.from(postService.findPostDetailInfo(blogId, popularId))).collect(Collectors.toList());
    }
}
