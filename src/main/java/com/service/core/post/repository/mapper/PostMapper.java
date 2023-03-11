package com.service.core.post.repository.mapper;

import com.service.core.post.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostCardDto> findRecentPostCardDto(Long blogId);

    List<PostDto> findTotalPostDtoListByPaging(PostSearchDto postSearchDto);

    List<PostDto> findCategoryPostDtoListByPaging(PostSearchDto postSearchDto);

    List<PostTitleDto> findCategoryPostTitleDtoListByPaging(PostSearchDto postSearchDto);

    int findPostCount(Long blogId);

    int findUndeletePostCount(Long blogId);

    int findPostCountByBlogCategory(Long blogId, Long categoryId);

    int findEqualPostCount(Long blogId, Long postId);

    List<PostLinkDto> findPostLinkDtoList(Long blogId, Long seq);

    List<PostSearchMapperDto> findPostDtoByKeyword(@Param("postKeywordSearchDto") PostKeywordSearchDto postKeywordSearchDto);

    List<PostSearchMapperDto> findPostDtoByTagKeyword(@Param("postTagKeywordSearchDto") PostTagKeywordSearchDto postTagKeywordSearchDto);

    List<PostCardDto> findRelatedPost(Long postId, Long blogId, Long categoryId, Long postSeq);

    int findPostDtoCountByKeyword(@Param("postKeywordSearchDto") PostKeywordSearchDto postKeywordSearchDto, Long blogId);

    int findPostDtoCountByTagKeyword(Long blogId, String keyword);
}
