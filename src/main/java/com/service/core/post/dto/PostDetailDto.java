package com.service.core.post.dto;

import com.service.core.category.domain.Category;
import com.service.core.post.domain.Post;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDto implements Serializable {
    private static final long serialVersionUID = ConstUtil.SERIAL_VERSION_ID;
    private Long id;
    private Long seq;
    private String title;
    private String contents;
    private String categoryName;
    private String thumbnailImage;
    private String writer;
    private Long categoryId;
    private Long blogId;
    private String registerTime;
    private LocalDateTime registerLocalDateTime;
    private String currentUrl;
    private List<String> tags;

    // TODO 좋아요, 조회수, etc 추가
    public static PostDetailDto from(Post post) {
        PostDetailDto postDetailDto = new PostDetailDto();
        postDetailDto.setId(post.getId());
        postDetailDto.setSeq(post.getSeq());
        postDetailDto.setTitle(post.getTitle());
        postDetailDto.setContents(post.getContents());
        postDetailDto.setThumbnailImage(post.getThumbnailImage());
        postDetailDto.setWriter(post.getWriter());
        postDetailDto.setCategoryName(post.getCategory().getName());
        postDetailDto.setCategoryId(post.getCategory().getId());
        postDetailDto.setBlogId(post.getBlog().getId());
        postDetailDto.setRegisterTime(BlogUtil.formatLocalDateTimeToStr(post.getRegisterTime()));
        postDetailDto.setRegisterLocalDateTime(post.getRegisterTime());
        postDetailDto.setCurrentUrl(BlogUtil.currentRequestUrl());
        postDetailDto.setTags(post.getTagList().stream().map(tag -> tag.getName()).collect(Collectors.toList()));
        return postDetailDto;
    }
}
