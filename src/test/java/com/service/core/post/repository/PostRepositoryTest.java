package com.service.core.post.repository;

import com.service.core.post.dto.*;
import com.service.core.post.model.BlogPostSearchInput;
import com.service.core.post.paging.PostPagination;
import com.service.core.post.paging.PostSearchPagingDto;
import com.service.core.post.repository.mapper.PostMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        List<PostCardDto> postCardDto = postMapper.findRecentPostCardDto(6L, 5);
        Assertions.assertNotNull(postCardDto);
    }

    @Test
    @Disabled
    public void findRelatedPostTest() {
        List<PostCardDto> postCardDtos = postMapper.findRelatedPost(53L, 9L, 19L, 2L);
        Assertions.assertNotNull(postCardDtos);
    }

    @Test
    public void postSummaryTest() throws ParseException {
        List<String> strList = new ArrayList<>();
        PostOverviewDto postOverviewDto3 = postMapper.findPostOverViewDtoById(167L);
        PostOverviewDto postOverviewDto1 = postMapper.findPostOverViewDtoById(174L);
        PostOverviewDto postOverviewDto6 = postMapper.findPostOverViewDtoById(162L);
        PostOverviewDto postOverviewDto2 = postMapper.findPostOverViewDtoById(157L);
        PostOverviewDto postOverviewDto4 = postMapper.findPostOverViewDtoById(21L);
        PostOverviewDto postOverviewDto5 = postMapper.findPostOverViewDtoById(29L);

        strList.add(postOverviewDto3.getRegisterTime());
        strList.add(postOverviewDto5.getRegisterTime());
        strList.add(postOverviewDto1.getRegisterTime());
        strList.add(postOverviewDto4.getRegisterTime());
        strList.add(postOverviewDto6.getRegisterTime());
        strList.add(postOverviewDto2.getRegisterTime());

        Collections.sort(strList, (o1, o2) -> {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date o1Date = new Date(simpleDateFormat.parse(o1).getTime());
                Date o2Date = new Date(simpleDateFormat.parse(o2).getTime());

                if (o1Date.getTime() > o2Date.getTime()) {
                    return -1;
                } else if (o1Date.getTime() == o2Date.getTime()) {
                    return 0;
                } else {
                    return 1;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage() + ", " + e);
                return 0;
            }
        });

        for (String str : strList) {
            System.out.println(str);
        }
    }
}
