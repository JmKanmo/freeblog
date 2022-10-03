package com.service.core.comment.domain;

import com.service.core.comment.model.CommentInput;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.persistence.Embeddable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentUser {
    private String userProfileImage;
    private String targetUserId;
    private String targetUserNickname;
    private boolean isOwner;
    private String userId;
    private String userPassword;
    private String userNickname;

    public static CommentUser from(CommentInput commentInput) {
        return CommentUser.builder()
                .userProfileImage(BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous()) ? "" : commentInput.getUserProfileImage())
                .targetUserId(commentInput.getTargetUserId())
                .targetUserNickname(commentInput.getTargetUserNickname())
                .isOwner(false)
                .userId(commentInput.getCommentUserId())
                .userPassword(BCrypt.hashpw(commentInput.getCommentUserPassword(), BCrypt.gensalt()))
                .userNickname(commentInput.getCommentUserNickname())
                .build();
    }
}
