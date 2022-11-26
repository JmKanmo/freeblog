package com.service.core.post.repository.mapper;

import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostLinkDto;
import com.service.core.post.dto.PostSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostDto> findTotalPostDtoListByPaging(PostSearchDto postSearchDto);

    List<PostDto> findCategoryPostDtoListByPaging(PostSearchDto postSearchDto);

    int findPostCount(Long blogId);

    int findPostCountByBlogCategory(Long blogId, Long categoryId);

    List<PostLinkDto> findPostLinkDtoList(Long blogId, Integer seq);
}
