package com.service.util.redis.service.popular;

import com.service.config.app.AppConfig;
import com.service.core.post.dto.PostCardDto;
import com.service.core.post.dto.PostDetailDto;
import com.service.core.post.service.PostService;
import com.service.util.domain.SortType;
import com.service.util.redis.service.like.PostLikeRedisTemplateService;
import com.service.util.redis.service.view.PostViewRedisTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostPopularTemplateService {
    private final PostLikeRedisTemplateService postLikeRedisTemplateService;
    private final PostViewRedisTemplateService postViewRedisTemplateService;
    private final PostService postService;

    private final AppConfig appConfig;

    public List<Long> getPopularPost(long blogId) {
        List<Long> postViewIdSet = postViewRedisTemplateService.getPostViewIdSet(blogId);
        List<Long> postLikeIdSet = postLikeRedisTemplateService.getPostLikeIdSet(blogId);
        Set<Long> popularIdSet = new HashSet<>();
        List<SortType<Long, Long, LocalDateTime>> sortTypes = new ArrayList<>();

        for (Long postId : postViewIdSet) {
            popularIdSet.add(postId);
        }

        for (Long postId : postLikeIdSet) {
            popularIdSet.add(postId);
        }

        for (Long popularId : popularIdSet) {
            int postLikeCount = postLikeRedisTemplateService.getPostLikeCount(popularId, blogId);
            long postViewCount = postViewRedisTemplateService.getPostViewCount(popularId, blogId);
            PostDetailDto postDetailDto = postService.findPostDetailInfo(blogId, popularId);
            sortTypes.add(SortType.from(popularId, postLikeCount + postViewCount, postDetailDto.getRegisterLocalDateTime()));
        }

        Collections.sort(sortTypes, (o1, o2) -> {
            if (o1.getSort1() == o2.getSort1()) {
                LocalDateTime o1LocalDateTime = o1.getSort2();
                LocalDateTime o2LocalDateTime = o2.getSort2();

                if (o1LocalDateTime != null && o2LocalDateTime != null) {
                    return o2LocalDateTime.isAfter(o1LocalDateTime) ? 1 : 0;
                } else {
                    return Long.compare(o2.getSort1(), o1.getSort1());
                }
            } else {
                return Long.compare(o2.getSort1(), o1.getSort1());
            }
        });

        return sortTypes.stream().map(SortType::getV).limit(appConfig.getRecentAndPopular_post_count()).collect(Collectors.toList());
    }
}
