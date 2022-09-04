package com.service.core.tag.service;

import com.service.core.post.domain.Post;
import com.service.core.tag.domain.Tag;
import com.service.core.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Transactional
    @Override
    public void register(List<String> tagStrList, Post post) {
        List<Tag> tagList = new ArrayList<>();

        for (String tagStr : tagStrList) {
            tagList.add(Tag.builder().name(tagStr).post(post).build());
        }

        tagRepository.saveAll(tagList);
    }
}
