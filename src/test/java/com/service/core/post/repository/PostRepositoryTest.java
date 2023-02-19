package com.service.core.post.repository;

import com.service.core.post.dto.PostCardDto;
import com.service.core.post.dto.PostKeywordSearchDto;
import com.service.core.post.dto.PostLinkDto;
import com.service.core.post.dto.PostSearchMapperDto;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.paging.PostPagination;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.core.post.repository.mapper.PostMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional(readOnly = true)
public class PostRepositoryTest {
    @Autowired
    PostMapper postMapper;

    @Test
    public void postMapperTest() {
        List<PostLinkDto> postLinkDtoList = postMapper.findPostLinkDtoList(3L, 0L);
        Assertions.assertNotNull(postLinkDtoList);
    }

    @Test
    public void findEqualPostCountTest() {
        int result = postMapper.findEqualPostCount(4L, 37L);
        System.out.println(result);
    }

    @Test
    public void findPostSearchByKeywordTest() {
        PostSearchPagingDto postSearchPagingDto = new PostSearchPagingDto();
        postSearchPagingDto.setPostPagination(new PostPagination(7, postSearchPagingDto));
        PostKeywordSearchDto postKeywordSearchDto = PostKeywordSearchDto.from(
                BlogPostSearchInput.builder().blogId(5L).keyword("좀비").build(),
                postSearchPagingDto,
                "LIKE"
        );
        List<PostSearchMapperDto> postDtoList = postMapper.findPostDtoByKeyword(postKeywordSearchDto);
        Assertions.assertNotNull(postDtoList);
    }

    @Test
    public void findPostSearchCountByKeywordTest() {
        PostKeywordSearchDto postKeywordSearchDto = PostKeywordSearchDto.from(
                BlogPostSearchInput.builder().blogId(5L).keyword("좀비").build(),
                null,
                "LIKE"
        );
        int postCount = postMapper.findPostDtoCountByKeyword(postKeywordSearchDto, 5L);
        System.out.println(postCount);
    }

    @Test
    public void findPostCardDtoTest() {
        List<PostCardDto> postCardDto = postMapper.findRecentPostCardDto(6L);
        Assertions.assertNotNull(postCardDto);
    }
}
