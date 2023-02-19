package com.service.core.tag.repository.mapper;

import com.service.core.tag.dto.TagDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    List<TagDto> findTagDtoList(Long blogId);
}
