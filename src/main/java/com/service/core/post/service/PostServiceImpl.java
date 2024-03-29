package com.service.core.post.service;

import com.service.config.app.AppConfig;
import com.service.config.sql.SqlConfig;
import com.service.core.category.domain.Category;
import com.service.core.category.service.CategoryService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.PostManageException;
import com.service.core.main.model.MainPostSearchInput;
import com.service.core.post.domain.Post;
import com.service.core.post.dto.*;
import com.service.core.post.model.BlogPostInput;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.model.BlogPostTagInput;
import com.service.core.post.model.BlogPostUpdateInput;
import com.service.core.post.repository.PostRepository;
import com.service.core.post.repository.mapper.PostMapper;
import com.service.core.tag.domain.Tag;
import com.service.core.tag.service.TagService;
import com.service.core.views.service.PostViewService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import com.service.util.aws.s3.AwsS3Service;
import com.service.core.post.paging.PostPagination;
import com.service.core.post.paging.PostPaginationResponse;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.util.redis.key.CacheKey;
import com.service.util.redis.service.like.PostLikeRedisTemplateService;
import com.service.util.sftp.SftpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;
    private final AwsS3Service awsS3Service;
    private final PostRepository postRepository; // Post 전체 정보를 불러옴 (DB 쿼리로 인한 contents 데이터 등등 메모리 증가 고려)
    private final TagService tagService;
    private final PostViewService postViewService;
    private final SftpService sftpService;
    private final SqlConfig sqlConfig;
    private final AppConfig appConfig;
    private final PostLikeRedisTemplateService postLikeRedisTemplateService;

    @Override
    public List<PostCardDto> findRecentPostCardDtoByBlogId(Long blogId) {
        return postMapper.findRecentPostCardDto(blogId, appConfig.getRecentAndPopular_post_count());
    }

    @Override
    public List<PostCardDto> findRelatedPost(Long postId, Long blogId, Long categoryId, Long postSeq) {
        return postMapper.findRelatedPost(postId, blogId, categoryId, postSeq);
    }

    @Override
    public PostPaginationResponse<PostKeywordDto> findMainPostSearchPaginationByKeyword(MainPostSearchInput mainPostSearchInput, PostSearchPagingDto postSearchPagingDto) {
        PostMainSearchDto postMainSearchDto = PostMainSearchDto.from(mainPostSearchInput, postSearchPagingDto, sqlConfig.getSqlSearchPattern());
        int postCount = postMapper.findPostMainSearchDtoCountByKeyword(postMainSearchDto);
        PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
        postSearchPagingDto.setPostPagination(postPagination);
        postMainSearchDto.setPostSearchPagingDto(postSearchPagingDto);
        return new PostPaginationResponse<>(PostKeywordDto.from(postMapper.findPostMainSearchDtoByKeyword(postMainSearchDto)), postPagination);
    }

    @Override
    public PostPaginationResponse<PostKeywordDto> findPostSearchPaginationByKeyword(BlogPostSearchInput blogPostSearchInput, PostSearchPagingDto postSearchPagingDto) {
        PostKeywordSearchDto postKeywordSearchDto = PostKeywordSearchDto.from(
                blogPostSearchInput,
                postSearchPagingDto,
                sqlConfig.getSqlSearchPattern()
        );
        int postCount = postMapper.findPostDtoCountByKeyword(postKeywordSearchDto, blogPostSearchInput.getBlogId());
        PostPagination postPagination = new PostPagination(postCount, postSearchPagingDto);
        postSearchPagingDto.setPostPagination(postPagination);
        postKeywordSearchDto.setPostSearchPagingDto(postSearchPagingDto);
        return new PostPaginationResponse<>(PostKeywordDto.from(postMapper.findPostDtoByKeyword(postKeywordSearchDto)), postPagination);
    }

    @Override
    public PostPaginationResponse<PostTagKeywordDto> findPostSearchPaginationByTagKeyword(BlogPostTagInput blogPostTagInput, PostSearchPagingDto postSearchPagingDto) {
        PostTagKeywordSearchDto postTagKeywordSearchDto = PostTagKeywordSearchDto.from(
                blogPostTagInput,
                postSearchPagingDto
        );
        int postCount = postMapper.findPostDtoCountByTagKeyword(blogPostTagInput.getBlogId(), blogPostTagInput.getTagKeyword());
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
    public String uploadSftpPostImage(MultipartFile multipartFile, String uploadKey) throws Exception {
        return sftpService.sftpFileUpload(multipartFile, ConstUtil.SFTP_POST_IMAGE_HASH, uploadKey);
    }

    @Override
    public String uploadSftpPostThumbnailImage(MultipartFile multipartFile, String uploadKey) throws Exception {
        return sftpService.sftpFileUpload(multipartFile, ConstUtil.SFTP_POST_THUMBNAIL_HASH, uploadKey);
    }

    @Override
    public void deleteSftpPostImage(List<String> imgSrcList) throws Exception {
        for (String imgSrc : imgSrcList) {
            sftpService.sftpFileDelete(imgSrc);
        }
    }

    @Override
    public void deleteSftpPostImage(String imgSrc) throws Exception {
        sftpService.sftpFileDelete(imgSrc);
    }

    @Transactional
    @Override
    public PostDetailDto register(Post post, BlogPostInput blogPostInput) {
        Post writedPost = postRepository.save(post);
        PostDetailDto postDetailDto = PostDetailDto.from(writedPost);
        List<String> tagStrList = BlogUtil.convertArrayToList(blogPostInput.getTag().split(","));
        postDetailDto.setTags(tagStrList);
        tagService.register(tagStrList, post);
        return postDetailDto;
    }

    @Transactional
    @Override
    @CachePut(value = CacheKey.POST_DETAIL_DTO, key = "#blogPostUpdateInput.blogId.toString() + '&' + #blogPostUpdateInput.postId.toString()")
    public PostDetailDto update(BlogPostUpdateInput blogPostUpdateInput, CategoryService categoryService) {
        Post post = findPostById(blogPostUpdateInput.getPostId());
        Category category = categoryService.findCategoryById(blogPostUpdateInput.getCategory());

        post.setTitle(blogPostUpdateInput.getTitle());
        post.setContents(blogPostUpdateInput.getContents());
        post.setSummary(blogPostUpdateInput.getSummary());
        post.setCategory(category);
        post.setThumbnailImage(BlogUtil.checkEmptyOrUndefinedStr(blogPostUpdateInput.getPostThumbnailImage()) ? ConstUtil.UNDEFINED : blogPostUpdateInput.getPostThumbnailImage());
        post.setMetaKey(blogPostUpdateInput.getMetaKey());
        tagService.update(post, post.getTagList(), BlogUtil.convertArrayToList(blogPostUpdateInput.getTag().split(",")));

        PostDetailDto updatedPostDetailDto = PostDetailDto.from(post);
        updatedPostDetailDto.setTags(BlogUtil.convertArrayToList(blogPostUpdateInput.getTag().split(",")));
        return updatedPostDetailDto;
    }


    @Override
    @CachePut(value = CacheKey.POST_DETAIL_DTO, key = "#blogId.toString() + '&' + #postId.toString()")
    public PostDetailDto updateCachePostDetailInfo(Long blogId, Long postId, PostDetailDto postDetailDto) {
        return postDetailDto;
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
    public PostDto findPostDtoInfo(Long blogId, Long postId) {
        PostDto postDto = postMapper.findPostDtoById2(postId, blogId);

        if (postDto == null) {
            throw new PostManageException(ServiceExceptionMessage.POST_NOT_FOUND);
        }
        return postDto;
    }

    @Override
    public PostOverviewDto findPostOverViewDtoById(Long postId) {
        PostOverviewDto postOverviewDto = postMapper.findPostOverViewDtoById(postId);

        if (postOverviewDto == null) {
            throw new PostManageException(ServiceExceptionMessage.POST_NOT_FOUND);
        }

        return postOverviewDto;
    }

    @Override
    public PostDto findPostDtoById(Long postId) {
        PostDto postDto = postMapper.findPostDtoById(postId);

        if (postDto == null) {
            throw new PostManageException(ServiceExceptionMessage.POST_NOT_FOUND);
        }

        return postDto;
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
        PostDto postDto = postMapper.findPostDtoById(postId);
        return postDto != null && postDto.getBlogId() == blogId;
    }

    @Transactional
    @Override
    @CacheEvict(value = CacheKey.POST_DETAIL_DTO, key = "#blogId.toString() + '&' + #postId.toString()")
    public void deletePost(Long blogId, Long postId) {
        Post post = findPostById(postId);
        post.setDelete(true);
        postViewService.deletePostView(blogId, postId);
        postLikeRedisTemplateService.deletePostLikeInfo(blogId, postId);
    }

    @Override
    public String viewPost(PostDetailDto postDetailDto) throws Exception {
        return BlogUtil.formatNumberComma(postViewService.viewPost(postDetailDto.getBlogId(), postDetailDto.getId()));
    }

    @Override
    public boolean isDeletedPost(long postId) {
        if (!postMapper.existsById(postId)) {
            return true;
        }

        PostDeleteDto postDeleteDto = postMapper.findPostDeleteDtoById(postId);

        if (postDeleteDto.isDelete()) {
            return true;
        }
        return false;
    }

    @Override
    public void postCheckRetrySleep(PostDetailDto postDetailDto) {
        for (int i = 1; i <= appConfig.getPostCheckRetryMaxCount(); i++) {
            try {
                if (findPostDetailInfo(postDetailDto.getBlogId(), postDetailDto.getId()) != null) {
                    return;
                }
            } catch (Exception e) {
                log.error("PostServiceImpl[postCheckRetrySleep] PostDetailDto Object not yet saved!!!, retry Count: " + i);
            }

            try {
                Thread.sleep(appConfig.getPostCheckRetrySleepTime());
            } catch (InterruptedException e) {
                log.error("PostServiceImpl[postCheckRetrySleep] sleep error!!!, retry Count: " + i);
            }
        }
    }

    private boolean checkPostId(Long blogId, Long postId) {
        PostDto postDto = postMapper.findPostDtoById2(postId, blogId);
        return postDto != null;
    }
}
