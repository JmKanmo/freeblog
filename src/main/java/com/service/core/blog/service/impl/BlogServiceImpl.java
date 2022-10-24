package com.service.core.blog.service.impl;

import com.service.core.blog.domain.Blog;
import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogInfoService;
import com.service.core.blog.service.BlogService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.BlogManageException;
import com.service.core.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {
    private final BlogInfoService blogInfoService;
    private final UserInfoService userInfoService;

    @Transactional
    @Override
    public Blog register(Blog blog) {
        return blogInfoService.register(blog);
    }

    @Override
    public BlogInfoDto findBlogInfoDtoById(String id) {
        Blog blog = userInfoService.findBlogByIdOrThrow(id);

        if (blog.isDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }
        return BlogInfoDto.fromEntity(blog);
    }

    @Override
    public Blog findBlogByEmail(String email) {
        Blog blog = userInfoService.findBlogByEmailOrThrow(email);

        if (blog.isDelete()) {
            throw new BlogManageException(ServiceExceptionMessage.ALREADY_DELETE_BLOG);
        }
        return blog;
    }

    @Override
    public Blog findBlogById(Long blogId) {
        return blogInfoService.findBlogByIdOrThrow(blogId);
    }
}
