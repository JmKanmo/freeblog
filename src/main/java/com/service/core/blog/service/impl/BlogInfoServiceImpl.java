package com.service.core.blog.service.impl;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.repository.BlogRepository;
import com.service.core.blog.service.BlogInfoService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogManageException;
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

    @Override
    public Blog findBlogByIdOrThrow(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND));

        if (blog.isDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }
        return blog;
    }
}
