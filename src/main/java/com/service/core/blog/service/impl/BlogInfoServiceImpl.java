package com.service.core.blog.service.impl;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.repository.BlogRepository;
import com.service.core.blog.service.BlogInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogInfoServiceImpl implements BlogInfoService {
    private final BlogRepository blogRepository;

    @Transactional
    @Override
    public Blog register(Blog blog) {
        return blogRepository.save(blog);
    }
}
