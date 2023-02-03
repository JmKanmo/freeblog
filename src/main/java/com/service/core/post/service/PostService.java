package com.service.core.post.service;

import com.service.core.category.service.CategoryService;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.*;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.model.BlogPostUpdateInput;
import com.service.core.post.paging.PostPaginationResponse;
import com.service.core.post.paging.PostSearchPagingDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface PostService {
    PostPaginationResponse<PostKeywordDto> findPostSearchPaginationByKeyword(BlogPostSearchInput blogPostSearchInput, PostSearchPagingDto postSearchPagingDto);

    PostPaginationResponse<PostTotalDto> findTotalPaginationPost(Long blogId, PostSearchPagingDto postSearchPagingDto, String type);

    String uploadAwsS3PostThumbnailImage(MultipartFile multipartFile) throws Exception;

    void register(Post post, BlogPostInput blogPostInput);

    void update(BlogPostUpdateInput blogPostUpdateInput, CategoryService categoryService);

    PostDetailDto findPostDetailInfo(Long blogId, Long postId);

    PostUpdateDto findPostUpdateInfo(Long blogId, Long postId);

    PostAlmostDto findPostAlmostInfo(Long blogId, Integer seq);

    List<PostDto> findPostPaginationById(PostSearchDto postSearchDto);

    int findPostCountByBlogCategory(Long blogId, Long categoryId);

    Post findPostById(Long postId);

    int findPostCountByBlogId(Long blogId);

    int findUndeletePostCountByBlogId(Long blogId);

    boolean checkEqualPostByLogin(Long blogId, Long postId);
}
