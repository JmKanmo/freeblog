package com.service.core.comment.domain;

import com.service.core.comment.model.CommentInput;
import com.service.core.post.domain.Post;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import com.service.util.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private Long parentId;

    @Column(length = 2000)
    private String comment;

    private String commentImage;

    private boolean secret;

    private boolean anonymous;

    @Embedded
    private CommentUser commentUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static Comment from(CommentInput commentInput, Post post) {
        return Comment.builder()
                .parentId(commentInput.getParentCommentId() == null ? 0 : commentInput.getParentCommentId())
                .comment(commentInput.getComment())
                .commentImage(commentInput.getCommentThumbnailImage() == null || commentInput.getCommentThumbnailImage().isEmpty() ? ConstUtil.UNDEFINED : commentInput.getCommentThumbnailImage())
                .secret(BlogUtil.parseAndGetCheckBox(commentInput.getSecretComment()))
                .anonymous(BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous()))
                .commentUser(CommentUser.from(commentInput))
                .post(post)
                .build();
    }
}
