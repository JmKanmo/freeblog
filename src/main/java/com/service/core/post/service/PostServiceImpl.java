package com.service.core.post.service;

import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.repository.PostRepository;
import com.service.core.post.repository.mapper.PostMapper;
import com.service.core.tag.service.TagService;
import com.service.util.BlogUtil;
import com.service.util.aws.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;
    private final AwsS3Service awsS3Service;
    private final PostRepository postRepository;

    private final TagService tagService;

    @Override
    public PostTotalDto findTotalPost(Long blogId, String type) {
        List<PostDto> postDtoList = postMapper.findPostDtoList(blogId);
        return PostTotalDto.fromPostDtoList(postDtoList, type);
    }

    @Override
    public String uploadAwsS3PostThumbnailImage(MultipartFile multipartFile) throws Exception {
        return awsS3Service.uploadImageFile(multipartFile);
    }

    @Transactional
    @Override
    public void register(Post post, BlogPostInput blogPostInput) {
        postRepository.save(post);
        tagService.register(BlogUtil.convertArrayToList(blogPostInput.getTag().split(",")), post);
    }
}
