package com.service.core.blog.service;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogServiceImpl implements BlogService {
    private final BlogRepository blogRepository;

    @Override
    public Blog register(Blog blog) {
        blogRepository.save(blog);
        return blog;
    }
}
