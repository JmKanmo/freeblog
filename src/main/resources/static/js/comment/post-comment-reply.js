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
                this.showSweetAlertInfoMessage("게시글을 발행 중입니다.", 3000);
                return;
            }

            if (this.checkReplyCommentForm() === false) {
                this.showSweetAlertInfoMessage("폼 입력 정보가 양식 조건에 유효하지 않습니다.", 3000);
                this.isCommentSubmitFlag = false;
                return;
            }

            const xhr = new XMLHttpRequest();
            const spinner = this.loadingSpin({
                lines: 15,
                length: 3,
                width: 3,
                radius: 3,
                scale: 1,
                corners: 1,
                color: '#000',
                opacity: 0.25,
                rotate: 0,
                direction: 1,
                speed: 1,
                trail: 60,
                fps: 20,
                zIndex: 2e9,
                className: 'spinner',
                top: '50%',
                left: '50%',
                shadow: false,
                hwaccel: false,
                position: 'absolute'
            }, "commentReplyLoading");
            xhr.open("POST", `/comment/reply`, true);
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

            xhr.addEventListener("loadend", evt => {
                const status = evt.target.status;
                const responseValue = evt.target.responseText;

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showSweetAlertErrorMessage(responseValue);
                    this.loadingStop(spinner, "commentReplyLoading");
                } else {
                    this.loadingStop(spinner, "commentReplyLoading");
                    const blogId = opener.document.getElementById("postSearchBlogIdInput").value;
                    const postId = opener.document.getElementById("postIdInput").value;
                    const href = document.getElementById("comment_href_input").value;
                    this.resetReplyCommentForm();
                    // window.opener.location.href = `/post/${postId}?blogId=${blogId}#${href}`;
                    window.opener.location.reload();
                    // this.showToastMessage(responseValue);
                }
                this.isCommentSubmitFlag = false;
            });

            xhr.addEventListener("error", event => {
                this.showSweetAlertErrorMessage('오류가 발생하여 답글 등록에 실패하였습니다.');
                this.loadingStop(spinner, "commentReplyLoading");
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


