package com.service.core.post.service;

import com.service.core.post.dto.PostDto;
import com.service.core.post.dto.PostTotalDto;
import com.service.core.post.repository.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;

    @Override
    public PostTotalDto findTotalPost(Long blogId, String type) {
        List<PostDto> postDtoList = postMapper.findPostDtoList(blogId);
        return PostTotalDto.fromPostDtoList(postDtoList, type);
    }
}
