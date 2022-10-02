package com.service.core.post.repository.mapper;

import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    List<PostDto> findPostDtoList(Long blogId);

    List<PostDto> findPostDtoListByPaging(PostSearchDto postSearchDto);

    int findPostCount(Long blogId);
}
