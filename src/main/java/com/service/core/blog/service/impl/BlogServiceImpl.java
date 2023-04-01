package com.service.core.blog.service.impl;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogInfoService;
import com.service.core.blog.service.BlogService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogManageException;
import com.service.core.user.service.UserInfoService;
import com.service.core.views.service.BlogVisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {
    private final BlogInfoService blogInfoService;
    private final UserInfoService userInfoService;
    private final BlogVisitorService blogVisitorService;

    @Transactional
    @Override
    public Blog register(Blog blog) {
        return blogInfoService.register(blog);
    }

    @Override
    public BlogInfoDto findBlogInfoDtoById(String id) {
        Blog blog = userInfoService.findBlogByIdOrThrow(id);

        if (blog == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blog.isDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }
        return BlogInfoDto.fromEntity(blog);
    }

    @Override
    public BlogInfoDto findBlogInfoDtoByEmail(String email) {
        Blog blog = findBlogByEmail(email);
        return BlogInfoDto.fromEntity(blog);
    }

    @Override
    public BlogInfoDto findBlogInfoDtoById(Long blogId) {
        Blog blog = findBlogByIdOrThrow(blogId);
        return BlogInfoDto.fromEntity(blog);
    }

    @Override
    public Blog findBlogByEmail(String email) {
        Blog blog = userInfoService.findBlogByEmailOrThrow(email);

        if (blog == null) {
            throw new BlogManageException(ServiceExceptionMessage.BLOG_NOT_FOUND);
        } else if (blog.isDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }
        return blog;
    }

    @Override
    public Blog findBlogByIdOrThrow(Long blogId) {
        return blogInfoService.findBlogByIdOrThrow(blogId);
    }

    @Override
    public boolean isDeleteOrNotFoundBlog(Long blogId) {
        try {
            return findBlogByIdOrThrow(blogId).isDelete();
        } catch (BlogManageException e) {
            return true;
        }
    }

    @Override
    public void visitBlog(int blogId, int visitorId) {
        blogVisitorService.visitBlog(blogId, visitorId);
    }
}
