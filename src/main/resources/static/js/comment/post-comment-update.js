class PostCommentUpdateController extends PostCommentCommonController {
    constructor() {
        super();
        this.postCommentUpdateForm = document.getElementById("post_comment_update_form");
        this.postCommentLockButton = document.getElementById("post_comment_lock_button");
        this.postCommentUpdateButton = document.getElementById("post_comment_update_button");
        this.authCheckPasswordInput = document.getElementById("auth_check_password_input");
        this.isAnonymous = document.getElementById("is_anonymous");
        this.isCommentSubmitFlag = false;
    }

    initEventListener() {
        super.initEventListener();

        if (this.postCommentLockButton != null) {
            this.postCommentLockButton.addEventListener("click", evt => {
                const isCommentLock = this.postCommentIsSecretInput.checked;

                if (isCommentLock === true) {
                    this.postCommentSecretImage.src = "/images/unlock.png";
                    this.postCommentIsSecretInput.checked = false;
                } else {
                    this.postCommentSecretImage.src = "/images/lock.png";
                    this.postCommentIsSecretInput.checked = true;
                }
            });
        }

        this.postCommentUpdateButton.addEventListener("click", evt => {
            if (this.isCommentSubmitFlag === true) {
                this.showToastMessage("게시글을 발행 중입니다.");
                return;
            }

            const xhr = new XMLHttpRequest();

            if (this.checkCommentUpdateForm() === false) {
                this.showToastMessage("폼 입력 정보가 양식 조건에 유효하지 않습니다.");
                this.isCommentSubmitFlag = false;
                return;
            }

            const isAnonymous = this.isAnonymous.value;

            if (isAnonymous === "true") {
                const authPassword = prompt("댓글 작성 시 입력한 비밀번호를 입력해주세요.");

                if (authPassword === null) {
                    this.isCommentSubmitFlag = false;
                    return;
                }

                this.authCheckPasswordInput.value = authPassword;
            }

            xhr.open("PUT", `/comment/update`, true);
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

            xhr.addEventListener("loadend", evt => {
                let status = evt.target.status;
                const responseValue = evt.target.responseText;

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(responseValue);
                } else {
                    const blogId = opener.document.getElementById("postSearchBlogIdInput").value;
                    const postId = opener.document.getElementById("postIdInput").value;
                    const href = document.getElementById("comment_href_input").value;
                    this.resetCommentUpdateForm();
                    // window.opener.location.href = `/post/${postId}?blogId=${blogId}#${href}`;
                    window.opener.location.reload();
                    // this.showToastMessage(responseValue);
                }
                this.isCommentSubmitFlag = false;
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('오류가 발생하여 댓글 수정에 실패하였습니다.');
            });
            xhr.send(new FormData(this.postCommentUpdateForm));
            this.isCommentSubmitFlag = true;
        });
    }

    checkCommentUpdateForm() {
        if (this.isAnonymous === "true") {
            if ((!this.commentUserNickname.value || this.commentUserNickname.value.match(/\s/g))
                || (!this.commentUserPassword || this.commentUserPassword.value.match(/\s/g))) {
                return false;
            }
        }
        return true;
    }

    resetCommentUpdateForm() {
        this.removeCommentImage();
        this.postCommentTextInput.value = "";
        this.currentTextCount.textContent = this.postCommentTextInput.value.length;
        if (this.isAnonymous === "true") {
            this.commentUserNickname.value = "";
            this.commentUserPassword.value = "";
        }
        this.postCommentIsSecretInput.checked = false;
        this.postCommentSecretImage.src = "/images/unlock.png";
    }

    initPostCommentUpdateController() {
        this.initEventListener();
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const postCommentUpdateController = new PostCommentUpdateController();
    postCommentUpdateController.initPostCommentUpdateController();
});

