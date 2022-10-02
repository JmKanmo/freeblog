package com.service.core.post.service;

import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.paging.PostPaginationResponse;
import com.service.core.post.paging.PostSearchDto;
import org.springframework.web.multipart.MultipartFile;


public interface PostService {
    PostTotalDto findTotalPost(Long blogId, String type);

    PostPaginationResponse<PostTotalDto> findTotalPaginationPost(Long blogId, PostSearchDto postSearchDto, String type);

    String uploadAwsS3PostThumbnailImage(MultipartFile multipartFile) throws Exception;

    void register(Post post, BlogPostInput blogPostInput);

    PostDetailDto findPostDetailInfo(Long blogId, Long postId);

    Post findPostById(Long postId);
}
