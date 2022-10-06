package com.service.core.comment.domain;

import com.service.core.comment.model.CommentInput;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.bcel.Const;
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
                .userProfileImage(BlogUtil.parseAndGetCheckBox(commentInput.getCommentIsAnonymous()) ? ConstUtil.UNDEFINED
                        : commentInput.getUserProfileImage() == null || commentInput.getUserProfileImage().isEmpty() ? ConstUtil.UNDEFINED : commentInput.getUserProfileImage())
                .targetUserId(commentInput.getTargetUserId() == null ? ConstUtil.UNDEFINED : commentInput.getTargetUserId())
                .targetUserNickname(commentInput.getTargetUserNickname() == null ? ConstUtil.UNDEFINED : commentInput.getTargetUserNickname())
                .isOwner(false)
                .userId(commentInput.getCommentUserId() == null ? ConstUtil.UNDEFINED: commentInput.getCommentUserId())
                .userPassword(BCrypt.hashpw(commentInput.getCommentUserPassword(), BCrypt.gensalt()))
                .userNickname(commentInput.getCommentUserNickname() == null ? ConstUtil.UNDEFINED : commentInput.getCommentUserNickname())
                .build();
    }
}
