package com.service.core.tag.service;

import com.service.core.post.domain.Post;
import com.service.core.tag.domain.Tag;
import com.service.core.tag.dto.TagDto;
import com.service.core.tag.repository.TagRepository;
import com.service.core.tag.repository.mapper.TagMapper;
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

    private final TagMapper tagMapper;

    public List<TagDto> findTagDtoList(Long blogId) {
        return tagMapper.findTagDtoList(blogId);
    }

    @Transactional
    @Override
    public void register(List<String> tagStrList, Post post) {
        List<Tag> tagList = new ArrayList<>();

        for (String tagStr : tagStrList) {
            tagList.add(Tag.builder().name(tagStr).post(post).build());
        }

        tagRepository.saveAll(tagList);
    }

    @Transactional
    @Override
    public void update(Post post, List<Tag> tagList, List<String> inputTagList) {
        List<Tag> delTagList = new ArrayList<>();
        List<Tag> newTagList = new ArrayList<>();

        for (Tag tag : tagList) {
            boolean isEqual = false;
            String deleteTag = null;

            for (String inputTag : inputTagList) {
                if (tag.getName().equals(inputTag)) {
                    isEqual = true;
                    deleteTag = inputTag;
                    break;
                }
            }

            if (isEqual == false) {
                delTagList.add(tag);
            } else {
                inputTagList.remove(deleteTag);
            }
        }

        for (String tagName : inputTagList) {
            newTagList.add(Tag.from(post, tagName));
        }

        delete(delTagList);
        save(newTagList);
    }

    @Transactional
    @Override
    public void save(List<Tag> tagList) {
        tagRepository.saveAll(tagList);
    }

    @Transactional
    @Override
    public void delete(List<Tag> tagList) {
        tagRepository.deleteAll(tagList);
    }
}
