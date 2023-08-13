package com.service.util.redis.service.popular;

import com.service.config.app.AppConfig;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.dto.PostOverviewDto;
import com.service.core.post.service.PostService;
import com.service.util.domain.SortType;
import com.service.util.redis.service.like.PostLikeRedisTemplateService;
import com.service.util.redis.service.view.PostViewRedisTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PostPopularTemplateService {
    private final PostLikeRedisTemplateService postLikeRedisTemplateService;
    private final PostViewRedisTemplateService postViewRedisTemplateService;
    private final PostService postService;

    private final AppConfig appConfig;

    public List<Long> getPopularPost(long blogId) throws Exception {
        List<Long> postViewIdSet = postViewRedisTemplateService.getPostViewIdSet(blogId);
        List<Long> postLikeIdSet = postLikeRedisTemplateService.getPostLikeIdSet(blogId);
        Set<Long> popularIdSet = new HashSet<>();
        List<SortType<Long, Long, String>> sortTypes = new ArrayList<>();

        for (Long postId : postViewIdSet) {
            popularIdSet.add(postId);
        }

        for (Long postId : postLikeIdSet) {
            popularIdSet.add(postId);
        }

        for (Long popularId : popularIdSet) {
            int postLikeCount = postLikeRedisTemplateService.getPostLikeCount(popularId, blogId);
            long postViewCount = postViewRedisTemplateService.getPostViewCount(popularId, blogId);
            if (!postService.isDeletedPost(popularId)) {
                PostOverviewDto postOverviewDto = postService.findPostOverViewDtoById(popularId);
                sortTypes.add(SortType.from(popularId, postLikeCount + postViewCount, postOverviewDto.getRegisterTime()));
            }
        }

        Collections.sort(sortTypes, (o1, o2) -> {
            try {
                if (o1.getSort1() == o2.getSort1()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date o1Date = new Date(simpleDateFormat.parse(o1.getSort2()).getTime());
                    Date o2Date = new Date(simpleDateFormat.parse(o2.getSort2()).getTime());

                    if (o1Date.getTime() > o2Date.getTime()) {
                        return -1;
                    } else if (o1Date.getTime() == o2Date.getTime()) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    return Long.compare(o2.getSort1(), o1.getSort1());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return 0;
            }
        });

        return sortTypes.stream().map(SortType::getV).limit(appConfig.getRecentAndPopular_post_count()).collect(Collectors.toList());
    }
}
