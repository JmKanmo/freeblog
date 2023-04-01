package com.service.core.post.service;

import com.service.config.app.AppConfig;
import com.service.config.sql.SqlConfig;
import com.service.core.blog.domain.Blog;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.PostManageException;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.*;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.model.BlogPostUpdateInput;
import com.service.core.post.repository.PostRepository;
import com.service.core.post.repository.mapper.PostMapper;
import com.service.core.tag.service.TagService;
import com.service.core.views.service.PostViewService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import com.service.util.aws.s3.AwsS3Service;
import com.service.core.post.paging.PostPagination;
import com.service.core.post.paging.PostPaginationResponse;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.util.redis.key.CacheKey;
import com.service.util.redis.service.popular.PostPopularTemplateService;
import com.service.util.sftp.SftpService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final PostViewService postViewService;

    private final PostPopularTemplateService postPopularTemplateService;
    private final SftpService sftpService;
    private final SqlConfig sqlConfig;
    private final AppConfig appConfig;

    @Override
    public List<PostCardDto> findRecentPostCardDtoByBlogId(Long blogId) {
        return postMapper.findRecentPostCardDto(blogId, appConfig.getRecentAndPopular_post_count());
    }

    @Override
    public List<PostCardDto> findPopularPostCardDtoByBlogId(Long blogId) {
        return null;
    }

    @Override
    public List<PostCardDto> findRelatedPost(Long postId, Long blogId, Long categoryId, Long postSeq) {
        return postMapper.findRelatedPost(postId, blogId, categoryId, postSeq);
    }

    @Override
    public PostPaginationResponse<PostKeywordDto> findPostSearchPaginationByKeyword(BlogPostSearchInput blogPostSearchInput, PostSearchPagingDto postSearchPagingDto) {
        PostKeywordSearchDto postKeywordSearchDto = PostKeywordSearchDto.from(
                blogPostSearchInput,
                null,
                sqlConfig.getSqlSearchPattern()
        );
        int postCount = postMapper.findPostDtoCountByKeyword(postKeywordSearchDto, blogPostSearchInput.getBlogId());
        PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
        postSearchPagingDto.setPostPagination(postPagination);
        postKeywordSearchDto.setPostSearchPagingDto(postSearchPagingDto);
        return new PostPaginationResponse<>(PostKeywordDto.from(postMapper.findPostDtoByKeyword(postKeywordSearchDto)), postPagination);
    }

    @Override
    public PostPaginationResponse<PostTagKeywordDto> findPostSearchPaginationByTagKeyword(BlogPostSearchInput blogPostSearchInput, PostSearchPagingDto postSearchPagingDto) {
        PostTagKeywordSearchDto postTagKeywordSearchDto = PostTagKeywordSearchDto.from(
                blogPostSearchInput,
                postSearchPagingDto
        );
        int postCount = postMapper.findPostDtoCountByTagKeyword(blogPostSearchInput.getBlogId(), blogPostSearchInput.getKeyword());
        PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
        postSearchPagingDto.setPostPagination(postPagination);
        postTagKeywordSearchDto.setPostSearchPagingDto(postSearchPagingDto);
        return new PostPaginationResponse<>(PostTagKeywordDto.from(postMapper.findPostDtoByTagKeyword(postTagKeywordSearchDto)), postPagination);
    }

    @Override
    public PostPaginationResponse<PostTotalDto> findTotalPaginationPost(Long blogId, PostSearchPagingDto postSearchPagingDto, String type) {
        int postCount = findUndeletePostCountByBlogId(blogId);
        PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
        postSearchPagingDto.setPostPagination(postPagination);
        return new PostPaginationResponse<>(PostTotalDto.fromPostDtoList(postMapper.findTotalPostDtoListByPaging(PostSearchDto.from(blogId, postSearchPagingDto)), postCount, type), postPagination);
    }

    @Override
    public String uploadAwsS3PostThumbnailImage(MultipartFile multipartFile) throws Exception {
        return awsS3Service.uploadImageFile(multipartFile);
    }

    @Override
    public String uploadSftpPostImage(MultipartFile multipartFile) throws Exception {
        return sftpService.sftpImageFileUpload(multipartFile);
    }

    @Override
    public void deleteSftpPostImage(List<String> imgSrcList) throws Exception {
        for (String imgSrc : imgSrcList) {
            sftpService.sftpImageFileDelete(imgSrc);
        }
    }

    @Transactional
    @Override
    public void register(Post post, BlogPostInput blogPostInput) {
        postRepository.save(post);
        tagService.register(BlogUtil.convertArrayToList(blogPostInput.getTag().split(",")), post);
    }

    @Transactional
    @Override
    @CacheEvict(value = CacheKey.POST_DETAIL_DTO, key = "#blogPostUpdateInput.blogId.toString() + '&' + #blogPostUpdateInput.postId.toString()")
    public void update(BlogPostUpdateInput blogPostUpdateInput, CategoryService categoryService) {
        Post post = findPostById(blogPostUpdateInput.getPostId());
        post.setTitle(blogPostUpdateInput.getTitle());
        post.setContents(blogPostUpdateInput.getContents());
        post.setCategory(categoryService.findCategoryById(blogPostUpdateInput.getCategory()));
        post.setThumbnailImage(BlogUtil.checkEmptyOrUndefinedStr(blogPostUpdateInput.getPostThumbnailImage()) ? ConstUtil.UNDEFINED : blogPostUpdateInput.getPostThumbnailImage());
        tagService.update(post, post.getTagList(), BlogUtil.convertArrayToList(blogPostUpdateInput.getTag().split(",")));
    }

    @Cacheable(value = CacheKey.POST_DETAIL_DTO, key = "#blogId.toString() + '&' + #postId.toString()")
    @Override
    public PostDetailDto findPostDetailInfo(Long blogId, Long postId) {
        if (!checkPostId(blogId, postId)) {
            throw new PostManageException(ServiceExceptionMessage.POST_NOT_FOUND);
        }
        return PostDetailDto.from(findPostById(postId));
    }

    @Override
    public PostDto findPostDtoById(Long postId) {
        return PostDto.fromEntity(findPostById(postId));
    }

    @Override
    public PostUpdateDto findPostUpdateInfo(Long blogId, Long postId) {
        if (!checkPostId(blogId, postId)) {
            throw new PostManageException(ServiceExceptionMessage.POST_NOT_FOUND);
        }
        return PostUpdateDto.from(findPostById(postId));
    }

    @Override
    public PostAlmostDto findPostAlmostInfo(Long blogId, Long seq) {
        return PostAlmostDto.from(seq, postMapper.findPostLinkDtoList(blogId, seq));
    }

    @Override
    public List<PostDto> findPostPaginationById(PostSearchDto postSearchDto) {
        return postMapper.findCategoryPostDtoListByPaging(postSearchDto);
    }

    @Override
    public List<PostTitleDto> findPostTitlePaginationById(PostSearchDto postSearchDto) {
        return postMapper.findCategoryPostTitleDtoListByPaging(postSearchDto);
    }

    @Override
    public int findPostCountByBlogCategory(Long blogId, Long categoryId) {
        return postMapper.findPostCountByBlogCategory(blogId, categoryId);
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

    @Override
    public int findPostCountByBlogId(Long blogId) {
        return postMapper.findPostCount(blogId);
    }

    @Override
    public int findUndeletePostCountByBlogId(Long blogId) {
        return postMapper.findUndeletePostCount(blogId);
    }

    @Override
    public boolean checkEqualPostByLogin(Long blogId, Long postId) {
        Post post = findPostById(postId);
        return post.getBlog().getId() == blogId;
    }

    @Transactional
    @Override
    @CacheEvict(value = CacheKey.POST_DETAIL_DTO, key = "#blogId.toString() + '&' + #postId.toString()")
    public void deletePost(Long blogId, Long postId) {
        Post post = findPostById(postId);
        post.setDelete(true);
    }

    @Override
    public String viewPost(PostDetailDto postDetailDto) {
        return BlogUtil.formatNumberComma(postViewService.viewPost(postDetailDto.getBlogId(), postDetailDto.getId()));
    }

    private boolean checkPostId(Long blogId, Long postId) {
        Post post = findPostById(postId);
        Blog blog = post.getBlog();
        return !blog.isDelete() && blog.getId() == blogId;
    }
}
