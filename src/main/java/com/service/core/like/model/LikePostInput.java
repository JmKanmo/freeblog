package com.service.core.like.model;

import com.service.core.post.dto.PostDetailDto;
import com.service.core.user.dto.UserHeaderDto;
import com.service.util.ConstUtil;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class LikePostInput {
    private String id;

    private String nickName; // 좋아요 누른 사용자 닉네임

    private String writer; // 좋아요 누른 게시글 작성자 닉네임

    private String title;

    private String userProfileThumbnailImage;

    private String postThumbnailImage;

    private Long postId;

    private Long blogId;

    public void setLikePostInput(UserHeaderDto userHeaderDto, PostDetailDto postDetailDto) {
        this.setId(userHeaderDto.getId());
        this.setNickName(userHeaderDto.getNickname());
        this.setWriter(postDetailDto.getWriter());
        this.setTitle(postDetailDto.getTitle());
        this.setUserProfileThumbnailImage(userHeaderDto.getProfileImages() == null ? ConstUtil.UNDEFINED : userHeaderDto.getProfileImages());
        this.setPostThumbnailImage(postDetailDto.getThumbnailImage());
    }
}
