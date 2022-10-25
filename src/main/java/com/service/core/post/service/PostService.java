package com.service.core.post.service;

import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostSearchDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.paging.PostPaginationResponse;
import com.service.core.post.paging.PostSearchPagingDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface PostService {
    PostPaginationResponse<PostTotalDto> findTotalPaginationPost(Long blogId, PostSearchPagingDto postSearchPagingDto, String type);

    String uploadAwsS3PostThumbnailImage(MultipartFile multipartFile) throws Exception;

    void register(Post post, BlogPostInput blogPostInput);

    PostDetailDto findPostDetailInfo(Long blogId, Long postId);

    List<PostDto> findPostPaginationById(PostSearchDto postSearchDto);

    int findPostCountByBlogCategory(Long blogId, Long categoryId);

    Post findPostById(Long postId);
}
