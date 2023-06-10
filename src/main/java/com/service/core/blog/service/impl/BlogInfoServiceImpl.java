package com.service.core.blog.service.impl;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogDeleteMapperDto;
import com.service.core.blog.dto.BlogMapperDto;
import com.service.core.blog.repository.BlogRepository;
import com.service.core.blog.repository.mapper.BlogMapper;
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
    private final BlogMapper blogMapper;

    @Transactional
    @Override
    public Blog register(Blog blog) {
        return blogRepository.save(blog);
    }

    @Override
    public Blog findBlogByIdOrThrow(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND));

        if (blog == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blog.isDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }
        return blog;
    }

    @Override
    public BlogMapperDto findBlogMapperDtoByIdOrThrow(Long blogId) {
        BlogMapperDto blogMapperDto = blogMapper.findBlogMapperDtoByBlogId(blogId);

        if (blogMapperDto == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blogMapperDto.getIsDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }

        return blogMapperDto;
    }

    @Override
    public BlogMapperDto findBlogMapperDtoByUserIdOrThrow(String userId) {
        BlogMapperDto blogMapperDto = blogMapper.findBlogMapperDtoByUserId(userId);

        if (blogMapperDto == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blogMapperDto.getIsDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }

        return blogMapperDto;
    }

    @Override
    public BlogMapperDto findBlogMapperDtoByEmailOrThrow(String email) {
        BlogMapperDto blogMapperDto = blogMapper.findBlogMapperDtoByEmail(email);

        if (blogMapperDto == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blogMapperDto.getIsDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }

        return blogMapperDto;
    }

    @Override
    public BlogDeleteMapperDto findBlogDeleteMapperDtoByIdOrThrow(Long blogId) {
        BlogDeleteMapperDto blogDeleteMapperDto = blogMapper.findBlogDeleteMapperDtoByBlogId(blogId);

        if (blogDeleteMapperDto == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blogDeleteMapperDto.getIsDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }

        return blogDeleteMapperDto;
    }

    @Override
    public BlogDeleteMapperDto findBlogDeleteMapperDtoByUserIdOrThrow(String userId) {
        BlogDeleteMapperDto blogDeleteMapperDto = blogMapper.findBlogDeleteMapperDtoByUserId(userId);

        if (blogDeleteMapperDto == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blogDeleteMapperDto.getIsDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }

        return blogDeleteMapperDto;
    }

    @Override
    public BlogDeleteMapperDto findBlogDeleteMapperDtoByEmailOrThrow(String email) {
        BlogDeleteMapperDto blogDeleteMapperDto = blogMapper.findBlogDeleteMapperDtoByEmail(email);

        if (blogDeleteMapperDto == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blogDeleteMapperDto.getIsDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }

        return blogDeleteMapperDto;
    }
}
