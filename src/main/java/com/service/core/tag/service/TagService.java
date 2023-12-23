package com.service.core.tag.service;

import com.service.core.post.domain.Post;
import com.service.core.tag.domain.Tag;
import com.service.core.tag.dto.TagDto;

import java.util.List;

public interface TagService {
    void register(List<String> tagStrList, Post post);

    void update(Post post, List<Tag> tagList, List<String> inputTagList);

    void save(List<Tag> tagList);

    void delete(List<Tag> tagList);

    List<TagDto> findTagDtoList(Long blogId);

    List<Tag> findTagListByPostId(Long postId);
}
