class PostCommentController extends PostCommentCommonController {
    constructor() {
        super();
        this.postCommentForm = document.getElementById("post_comment_form");
        this.postCommentSubmitButton = document.getElementById("post_comment_submit_button");
        this.isClickedPostCommentSubmuitButton = false;

        this.postCommentList = document.getElementById("post_comment_list");
        this.commentPagination = document.getElementById("commentPagination");

        this.commentRecordSize = 20;
        this.commentPageSize = 10;

        this.lastClickedPage = null;
        this.lastClickedUrl = null;
    }

    initEventListener() {
        super.initEventListener();

        this.postCommentSubmitButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();

            if (this.checkCommentForm() === false) {
                this.showToastMessage("폼 입력 정보가 양식 조건에 유효하지 않습니다.");
                return;
            } else if (this.isClickedPostCommentSubmuitButton === true) {
                this.showToastMessage("댓글 등록이 진행 중입니다.");
                return;
            }

            xhr.open("POST", `/comment/register`, true);
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
                this.isClickedPostCommentSubmuitButton = true;
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
                    this.lastClickedUrl = url;
                    this.lastClickedPage = page;
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
                this.openPopUp(1070, 360, `/comment/update/${commentId}`, 'popup');
            } else if (commentDeleteButton != null) {
                const commentId = commentDeleteButton.closest(".post_comment_util_block").getAttribute("commentId");
                if (confirm('댓글을 삭제하시겠습니까?')) {
                    this.#deleteComment(commentId);
                }
            } else if (commentReplyButton != null) {
                const commentId = commentReplyButton.closest(".post_comment_util_block").getAttribute("commentId");
                this.openPopUp(1070, 360, `/comment/reply/${commentId}`, 'popup');
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

    #deleteComment(commentId) {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", `/comment/authority/${commentId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue["auth"] === false) {
                    this.showToastMessage("댓글을 삭제할 권한이 없습니다. 로그인 후 시도해 주세요.");
                    return;
                }

                if (responseValue["login"] === false) {
                    const password = prompt("댓글 작성시 입력한 비밀번호를 입력하세요.");

                    if (password == null) {
                        return;
                    }
                    const subXhr = new XMLHttpRequest();

                    subXhr.open("DELETE", `/comment/delete/${commentId}?password=${password}`, true);

                    subXhr.addEventListener("loadend", evt => {
                        let status = evt.target.status;
                        const responseValue = evt.target.responseText;

                        this.showToastMessage(responseValue);

                        if (status >= 400 && status <= 500) {
                            return;
                        }
                        this.requestComment();
                    });

                    subXhr.addEventListener("error", event => {
                        this.showToastMessage("댓글 삭제 작업에 실패하였습니다.");
                    });

                    subXhr.send();
                } else {
                    const subXhr = new XMLHttpRequest();
                    subXhr.open("DELETE", `/comment/delete/${commentId}`, true);

                    subXhr.addEventListener("loadend", event => {
                        let status = event.target.status;
                        const responseValue = event.target.responseText;

                        this.showToastMessage(responseValue);

                        if (status >= 400 && status <= 500) {
                            return;
                        }
                        this.requestComment();
                    });

                    subXhr.addEventListener("error", event => {
                        this.showToastMessage("댓글 삭제 작업에 실패하였습니다.");
                    });

                    subXhr.send();
                }
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("댓글 삭제 권한 확인 작업에 실패하였습니다.");
        });

        xhr.send();
    }

    requestComment() {
        if (!this.lastClickedUrl && !this.lastClickedPage) {
            this.#requestComment(`/comment/${this.postCommentPostId.value}/${this.postCommentBlogId.value}`);
        } else {
            this.#requestComment(this.lastClickedUrl, this.lastClickedPage);
        }
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
        this.commentPagination.innerHTML = this.drawBasicPagination(pagination, queryParam, url);
    }

    #requestComment(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.commentRecordSize, this.commentPageSize);

        xhr.open("GET", url + '?' + queryParam.toString(), true);


        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleTemplateList(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["commentPaginationResponse"]["commentPagination"], queryParam, url);
                // 게시글 상세 화면에서 url이 #href일 경우  특정 댓글 위치로 스크롤
                this.scrollTargetElement(this.getUrlStrAndParse('#'));
            }
            this.isClickedPostCommentSubmuitButton = false;
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