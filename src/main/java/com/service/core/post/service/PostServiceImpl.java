package com.service.core.post.service;

import com.service.core.blog.domain.Blog;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.PostManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostSearchDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.repository.PostRepository;
import com.service.core.post.repository.mapper.PostMapper;
import com.service.core.tag.service.TagService;
import com.service.util.BlogUtil;
import com.service.util.aws.s3.AwsS3Service;
import com.service.core.post.paging.PostPagination;
import com.service.core.post.paging.PostPaginationResponse;
import com.service.core.post.paging.PostSearchPagingDto;
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
    public PostPaginationResponse<PostTotalDto> findTotalPaginationPost(Long blogId, PostSearchPagingDto postSearchPagingDto, String type) {
        int postCount = postMapper.findPostCount(blogId);
        PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
        postSearchPagingDto.setPostPagination(postPagination);
        return new PostPaginationResponse<>(PostTotalDto.fromPostDtoList(postMapper.findPostDtoListByPaging(PostSearchDto.from(blogId, postSearchPagingDto)), postCount, type), postPagination);
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

    @Override
    public PostDetailDto findPostDetailInfo(Long blogId, Long postId) {
        if (!checkPostId(blogId, postId)) {
            throw new PostManageException(ServiceExceptionMessage.POST_NOT_FOUND);
        }
        return PostDetailDto.from(findPostById(postId));
    }

    @Override
    public Post findPostById(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostManageException(ServiceExceptionMessage.POST_NOT_FOUND);
        }
        Post post = postRepository.findById(postId).get();

        if (post.isDelete()) {
            throw new PostManageException(ServiceExceptionMessage.ALREADY_DELETE_POST);
        }
        return post;
    }

    private boolean checkPostId(Long blogId, Long postId) {
        Post post = findPostById(postId);
        Blog blog = post.getBlog();
        return !blog.isDelete() && blog.getId() == blogId;
    }
}
