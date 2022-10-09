class PostCommentController extends PostCommentCommonController {
    constructor() {
        super();
        this.postCommentForm = document.getElementById("post_comment_form");
        this.postCommentSubmitButton = document.getElementById("post_comment_submit_button");

        this.postCommentList = document.getElementById("post_comment_list");
        this.commentPagination = document.getElementById("commentPagination");

        this.commentRecordSize = 20;
        this.commentPageSize = 10;
    }

    initEventListener() {
        super.initEventListener();

        this.postCommentSubmitButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();

            if (this.checkCommentForm() === false) {
                this.showToastMessage("폼 입력 정보가 양식 조건에 유효하지 않습니다.");
                return;
            }

            xhr.open("POST", `/comment/register`);
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

            xhr.addEventListener("loadend", evt => {
                let status = evt.target.status;
                const responseValue = JSON.parse(evt.target.responseText);

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(responseValue["message"]);
                } else {
                    this.resetCommentForm();
                    const commentCount = responseValue["commentCount"];
                    this.#requestComment(`/comment/${this.postCommentPostId.value}/${this.postCommentBlogId.value}`, Math.ceil((commentCount / this.commentRecordSize)));
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('오류가 발생하여 댓글 등록에 실패하였습니다.');
            });
            xhr.send(new FormData(this.postCommentForm));
        });

        this.commentPagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestComment(url, page);
                }
            }
        });

        this.postCommentList.addEventListener("click", evt => {
            const commentUpdateButton = evt.target.closest(".post_comment_update_button");
            const commentDeleteButton = evt.target.closest(".post_comment_delete_button");
            const commentReplyButton = evt.target.closest(".post_comment_reply_button");
            // TODO 신고 버튼 추후에 추가

            if (commentUpdateButton != null) {
                const commentId = commentUpdateButton.closest(".post_comment_util_block").getAttribute("commentId");
                this.openPopUp(1070, 360, `/comment/update/${commentId}`, 'popup')
            } else if (commentDeleteButton != null) {
                const commentId = commentDeleteButton.closest(".post_comment_util_block").getAttribute("commentId");
            } else if (commentReplyButton != null) {
                const commentId = commentReplyButton.closest(".post_comment_util_block").getAttribute("commentId");
            }
        });

        if (this.postCommentLockButton != null) {
            this.postCommentLockButton.addEventListener("click", evt => {
                const isCommentLock = this.postCommentIsSecretInput.checked;

                if (isCommentLock === true) {
                    this.postCommentSecretImage.src = "../images/unlock.png";
                    this.postCommentIsSecretInput.checked = false;
                } else {
                    this.postCommentSecretImage.src = "../images/lock.png";
                    this.postCommentIsSecretInput.checked = true;
                }
            });
        }
    }

    requestComment() {
        this.#requestComment(`/comment/${this.postCommentPostId.value}/${this.postCommentBlogId.value}`);
    }

    checkCommentForm() {
        if (this.commentIsAnonymous.checked === true) {
            if ((!this.commentUserNickname.value || this.commentUserNickname.value.match(/\s/g))
                || (!this.commentUserPassword || this.commentUserPassword.value.match(/\s/g))) {
                return false;
            }
            return true;
        }
    }

    #handleTemplateList(responseValue) {
        const postCommentTemplate = document.getElementById("post-comment-template").innerHTML;
        const postCommentTemplateObject = Handlebars.compile(postCommentTemplate);
        const postCommentTemplateHTML = postCommentTemplateObject(responseValue["commentPaginationResponse"]["commentSummaryDto"]);
        this.postCommentList.innerHTML = postCommentTemplateHTML;
    }

    #clearPagination() {
        this.commentPagination.innerHTML = ``;
    }

    #handlePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.commentPagination.innerHTML = '';
            return;
        }
        this.commentPagination.innerHTML = this.drawPagination(pagination, queryParam, url);
    }

    #requestComment(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.commentRecordSize, this.commentPageSize);

        xhr.open("GET", url + '?' + queryParam.toString());


        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleTemplateList(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["commentPaginationResponse"]["commentPagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("댓글 정보를 불러오는데 실패하였습니다.");
        });

        xhr.send();
    }

    resetCommentForm() {
        this.removeCommentImage();
        this.postCommentTextInput.value = "";
        this.currentTextCount.textContent = this.postCommentTextInput.value.length;
        this.commentUserNickname.value = "";
        this.commentUserPassword.value = "";
        this.postCommentIsSecretInput.checked = false;
        this.postCommentSecretImage.src = "../images/unlock.png";
        this.commentIsAnonymous.checked = false;
    }
}