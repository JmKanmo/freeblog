package com.service.core.post.repository.mapper;

import com.service.core.post.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostDto> findTotalPostDtoListByPaging(PostSearchDto postSearchDto);

    List<PostDto> findCategoryPostDtoListByPaging(PostSearchDto postSearchDto);

    int findPostCount(Long blogId);

    int findUndeletePostCount(Long blogId);

    int findPostCountByBlogCategory(Long blogId, Long categoryId);

    int findEqualPostCount(Long blogId, Long postId);

    List<PostLinkDto> findPostLinkDtoList(Long blogId, Long seq);

    List<PostSearchMapperDto> findPostDtoByKeyword(@Param("postKeywordSearchDto") PostKeywordSearchDto postKeywordSearchDto);

    int findPostDtoCountByKeyword(@Param("postKeywordSearchDto") PostKeywordSearchDto postKeywordSearchDto, Long blogId);
}
