package com.service.core.post.service;

import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.model.BlogPostInput;
import org.springframework.web.multipart.MultipartFile;


public interface PostService {
    PostTotalDto findTotalPost(Long blogId, String type);

    String uploadAwsS3PostThumbnailImage(MultipartFile multipartFile) throws Exception;

    void register(Post post, BlogPostInput blogPostInput);
}
