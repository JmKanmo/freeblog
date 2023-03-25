package com.service.core.views.service;

import com.service.core.views.dto.BlogVisitorsDto;
import com.service.util.redis.service.view.BlogViewRedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BlogVisitorService {
    private final BlogViewRedisTemplateService blogViewRedisTemplateService;

    public void visitBlog(int blogId, int visitorId) {
        blogViewRedisTemplateService.visitBlog(blogId, visitorId);
    }

    public BlogVisitorsDto getBlogVisitorDto(int blogId) {
        return BlogVisitorsDto.from(blogViewRedisTemplateService.getBlogVisitors(blogId));
    }
}
