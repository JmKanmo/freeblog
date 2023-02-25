class PostCommentReplyController extends PostCommentCommonController {
    constructor() {
        super();
        this.postCommentReplyForm = document.getElementById("post_comment_reply_form");
        this.postReplyCommentButton = document.getElementById("post_reply_comment_button");
        this.isCommentSubmitFlag = false;
    }

    initPostCommentReplyController() {
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

        this.postReplyCommentButton.addEventListener("click", evt => {
            if (this.isCommentSubmitFlag === true) {
                this.showToastMessage("게시글을 발행 중입니다.");
                return;
            }

            const xhr = new XMLHttpRequest();

            if (this.checkReplyCommentForm() === false) {
                this.showToastMessage("폼 입력 정보가 양식 조건에 유효하지 않습니다.");
                this.isCommentSubmitFlag = false;
                return;
            }

            xhr.open("POST", `/comment/reply`, true);
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

            xhr.addEventListener("loadend", evt => {
                const status = evt.target.status;
                const responseValue = evt.target.responseText;

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(responseValue);
                } else {
                    this.resetReplyCommentForm();
                    this.showToastMessage(responseValue);
                }
                this.isCommentSubmitFlag = false;
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('오류가 발생하여 답글 등록에 실패하였습니다.');
                this.isCommentSubmitFlag = false;
            });
            xhr.send(new FormData(this.postCommentReplyForm));
            this.isCommentSubmitFlag = true;
        });
    }

    checkReplyCommentForm() {
        if (this.commentIsAnonymous.checked === true) {
            if ((!this.commentUserNickname.value || this.commentUserNickname.value.match(/\s/g))
                || (!this.commentUserPassword || this.commentUserPassword.value.match(/\s/g))) {
                return false;
            }
            return true;
        }
    }

    resetReplyCommentForm() {
        this.removeCommentImage();
        this.postCommentTextInput.value = "";
        this.currentTextCount.textContent = this.postCommentTextInput.value.length;
        this.commentUserNickname.value = "";
        this.commentUserPassword.value = "";
        this.postCommentIsSecretInput.checked = false;
        this.postCommentSecretImage.src = "/images/unlock.png";
        this.commentIsAnonymous.checked = false;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const postCommentReplyController = new PostCommentReplyController();
    postCommentReplyController.initPostCommentReplyController();
});


