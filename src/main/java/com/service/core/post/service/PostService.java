package com.service.core.post.service;

import com.service.core.category.service.CategoryService;
import com.service.core.main.model.MainPostSearchInput;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.*;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.model.BlogPostTagInput;
import com.service.core.post.model.BlogPostUpdateInput;
import com.service.core.post.paging.PostPaginationResponse;
import com.service.core.post.paging.PostSearchPagingDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface PostService {
    List<PostCardDto> findRecentPostCardDtoByBlogId(Long blogId);

    List<PostCardDto> findRelatedPost(Long postId, Long blogId, Long categoryId, Long postSeq);

    PostPaginationResponse<PostKeywordDto> findMainPostSearchPaginationByKeyword(MainPostSearchInput mainPostSearchInput, PostSearchPagingDto postSearchPagingDto);

    PostPaginationResponse<PostKeywordDto> findPostSearchPaginationByKeyword(BlogPostSearchInput blogPostSearchInput, PostSearchPagingDto postSearchPagingDto);

    PostPaginationResponse<PostTagKeywordDto> findPostSearchPaginationByTagKeyword(BlogPostTagInput blogPostTagInput, PostSearchPagingDto postSearchPagingDto);

    PostPaginationResponse<PostTotalDto> findTotalPaginationPost(Long blogId, PostSearchPagingDto postSearchPagingDto, String type);

    String uploadAwsS3PostThumbnailImage(MultipartFile multipartFile) throws Exception;

    String uploadSftpPostImage(MultipartFile multipartFile, String uploadKey) throws Exception;

    String uploadSftpPostThumbnailImage(MultipartFile multipartFile, String uploadKey) throws Exception;

    void deleteSftpPostImage(List<String> imgSrc) throws Exception;

    void deleteSftpPostImage(String imgSrc) throws Exception;

    PostDetailDto register(Post post, BlogPostInput blogPostInput);

    PostDetailDto update(BlogPostUpdateInput blogPostUpdateInput, CategoryService categoryService);

    PostDetailDto updateCachePostDetailInfo(Long blogId, Long postId, Post post);

    PostDetailDto findPostDetailInfo(Long blogId, Long postId);

    PostDto findPostDtoInfo(Long blogId, Long postId);

    PostOverviewDto findPostOverViewDtoById(Long postId);

    PostDto findPostDtoById(Long postId);

    PostUpdateDto findPostUpdateInfo(Long blogId, Long postId);

    PostAlmostDto findPostAlmostInfo(Long blogId, Long seq);

    List<PostDto> findPostPaginationById(PostSearchDto postSearchDto);

    List<PostTitleDto> findPostTitlePaginationById(PostSearchDto postSearchDto);

    int findPostCountByBlogCategory(Long blogId, Long categoryId);

    Post findPostById(Long postId);

    int findPostCountByBlogId(Long blogId);

    int findUndeletePostCountByBlogId(Long blogId);

    boolean checkEqualPostByLogin(Long blogId, Long postId);

    void deletePost(Long blogId, Long postId);

    String viewPost(PostDetailDto postDetailDto) throws Exception;

    boolean isDeletedPost(long postId);
}
