package com.service.core.post.repository.mapper;

import com.service.core.post.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostCardDto> findRecentPostCardDto(Long blogId, int recentPopularPostCount);

    List<PostDto> findTotalPostDtoListByPaging(PostSearchDto postSearchDto);

    List<PostDto> findCategoryPostDtoListByPaging(PostSearchDto postSearchDto);

    List<PostTitleDto> findCategoryPostTitleDtoListByPaging(PostSearchDto postSearchDto);

    PostDeleteDto findPostDeleteDtoById(Long postId);

    PostDto findPostDtoById(Long postId);

    PostDto findPostDtoById2(Long postId, Long blogId);

    PostOverviewDto findPostOverViewDtoById(Long postId);

    boolean existsById(Long postId);

    int findPostCount(Long blogId);

    int findUndeletePostCount(Long blogId);

    int findPostCountByBlogCategory(Long blogId, Long categoryId);

    int findEqualPostCount(Long blogId, Long postId);

    List<PostLinkDto> findPostLinkDtoList(Long blogId, Long seq);

    List<PostSearchMapperDto> findPostDtoByKeyword(@Param("postKeywordSearchDto") PostKeywordSearchDto postKeywordSearchDto);

    List<PostSearchMapperDto> findPostDtoByTagKeyword(@Param("postTagKeywordSearchDto") PostTagKeywordSearchDto postTagKeywordSearchDto);

    List<PostCardDto> findRelatedPost(Long postId, Long blogId, Long categoryId, Long postSeq);

    int findPostDtoCountByKeyword(@Param("postKeywordSearchDto") PostKeywordSearchDto postKeywordSearchDto, Long blogId);

    int findPostMainSearchDtoCountByKeyword(@Param("postMainSearchDto") PostMainSearchDto postMainSearchDto);

    List<PostSearchMapperDto> findPostMainSearchDtoByKeyword(@Param("postMainSearchDto") PostMainSearchDto postMainSearchDto);

    int findPostDtoCountByTagKeyword(Long blogId, String keyword);
}
