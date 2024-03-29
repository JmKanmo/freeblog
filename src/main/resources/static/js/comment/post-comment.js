class PostCommentController extends PostCommentCommonController {
    constructor() {
        super();
        this.postCommentForm = document.getElementById("post_comment_form");
        this.postCommentSubmitButton = document.getElementById("post_comment_submit_button");
        this.isClickedPostCommentSubmuitButton = false;
        this.isClickedPostCommentDeleteButton = false;

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
            if (this.isClickedPostCommentSubmuitButton === true) {
                this.showSweetAlertInfoMessage("댓글 등록이 진행 중입니다.", 3000);
                return;
            }

            const xhr = new XMLHttpRequest();
            const spinner = this.loadingSpin({
                lines: 15, length: 5, width: 5, radius: 8, scale: 1,
                corners: 1, color: '#000', opacity: 0.25, rotate: 0, direction: 1, speed: 1,
                trail: 60, fps: 20, zIndex: 2e9, className: 'spinner', top: '50%', left: '50%',
                shadow: false, hwaccel: false, position: 'absolute'
            }, "commentRegisterLoading");

            if (this.checkCommentForm() === false) {
                this.showSweetAlertWarningMessage("폼 입력 정보가 양식 조건에 유효하지 않습니다.");
                this.loadingStop(spinner, "commentRegisterLoading");
                return;
            }

            xhr.open("POST", `/comment/register`, true);
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

            xhr.addEventListener("loadend", evt => {
                let status = evt.target.status;
                const responseValue = JSON.parse(evt.target.responseText);

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.loadingStop(spinner, "commentRegisterLoading");
                    this.showSweetAlertErrorMessage(responseValue["message"]);
                } else {
                    this.loadingStop(spinner, "commentRegisterLoading");
                    this.resetCommentForm();
                    const commentCount = responseValue["commentCount"];
                    const commentId = responseValue["commentId"];
                    this.#checkComment(commentId, commentCount);
                }
                this.isClickedPostCommentSubmuitButton = false;
            });

            xhr.addEventListener("error", event => {
                this.showSweetAlertErrorMessage('오류가 발생하여 댓글 등록에 실패하였습니다.');
                this.loadingStop(spinner, "commentRegisterLoading");
                this.isClickedPostCommentSubmuitButton = false;
            });

            xhr.send(new FormData(this.postCommentForm));
            this.isClickedPostCommentSubmuitButton = true;
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
                const subPostList = commentUpdateButton.closest(".sub_post_list");
                let href = null;

                if (subPostList == null) {
                    const postCommentBlockList = commentUpdateButton.closest(".post_comment_block");
                    if (postCommentBlockList != null) {
                        href = postCommentBlockList.getAttribute("id");
                    }
                } else {
                    href = subPostList.getAttribute("id");
                }
                this.openPopUp(1070, 360, `/comment/update/${commentId}/${href}`, 'popup');
            } else if (commentDeleteButton != null) {
                const commentId = commentDeleteButton.closest(".post_comment_util_block").getAttribute("commentId");
                if (confirm('댓글을 삭제하시겠습니까?')) {
                    this.#deleteComment(commentId);
                }
            } else if (commentReplyButton != null) {
                const commentId = commentReplyButton.closest(".post_comment_util_block").getAttribute("commentId");
                const subPostList = commentReplyButton.closest(".sub_post_list");
                let href = null;

                if (subPostList == null) {
                    const postCommentBlockList = commentReplyButton.closest(".post_comment_block");
                    if (postCommentBlockList != null) {
                        href = postCommentBlockList.getAttribute("id");
                    }
                } else {
                    href = subPostList.getAttribute("id");
                }
                this.openPopUp(1070, 360, `/comment/reply/${commentId}/${href}`, 'popup');
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
        if (this.isClickedPostCommentDeleteButton === true) {
            this.showSweetAlertInfoMessage("댓글을 삭제 중입니다.", 3000);
            return;
        }

        const xhr = new XMLHttpRequest();
        xhr.open("GET", `/comment/authority/${commentId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showSweetAlertErrorMessage(responseValue["message"]);
                this.isClickedPostCommentDeleteButton = false;
            } else {
                if (responseValue["auth"] === false) {
                    this.showSweetAlertErrorMessage("댓글을 삭제할 권한이 없습니다. 로그인 후 시도해 주세요.");
                    this.isClickedPostCommentDeleteButton = false;
                    return;
                }

                if (responseValue["login"] === false) {
                    const password = prompt("댓글 작성시 입력한 비밀번호를 입력하세요.");

                    if (password == null) {
                        this.isClickedPostCommentDeleteButton = false;
                        return;
                    }
                    const subXhr = new XMLHttpRequest();
                    const spinner2 = this.loadingSpin({
                        lines: 15,
                        length: 5,
                        width: 5,
                        radius: 8,
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
                        position: 'fixed'
                    }, "commentDeleteLoading");
                    subXhr.open("DELETE", `/comment/delete/${commentId}?password=${password}`, true);

                    subXhr.addEventListener("loadend", evt => {
                        let status = evt.target.status;
                        const responseValue = evt.target.responseText;

                        this.showSweetAlertErrorMessage(responseValue);

                        if (status >= 400 && status <= 500) {
                            this.loadingStop(spinner2, "commentDeleteLoading");
                            this.isClickedPostCommentDeleteButton = false;
                            return;
                        }
                        this.loadingStop(spinner2, "commentDeleteLoading");
                        this.requestComment();
                        this.isClickedPostCommentDeleteButton = false;
                    });

                    subXhr.addEventListener("error", event => {
                        this.showSweetAlertErrorMessage("댓글 삭제 작업에 실패하였습니다.");
                        this.loadingStop(spinner2, "commentDeleteLoading");
                        this.isClickedPostCommentDeleteButton = false;
                    });

                    subXhr.send();
                } else {
                    const subXhr = new XMLHttpRequest();
                    const spinner2 = this.loadingSpin({
                        lines: 15,
                        length: 5,
                        width: 5,
                        radius: 8,
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
                        position: 'fixed'
                    }, "commentDeleteLoading");
                    subXhr.open("DELETE", `/comment/delete/${commentId}`, true);

                    subXhr.addEventListener("loadend", event => {
                        let status = event.target.status;
                        const responseValue = event.target.responseText;

                        this.showSweetAlertErrorMessage(responseValue);

                        if (status >= 400 && status <= 500) {
                            this.loadingStop(spinner2, "commentDeleteLoading");
                            this.isClickedPostCommentDeleteButton = false;
                            return;
                        }
                        this.loadingStop(spinner2, "commentDeleteLoading");
                        this.requestComment();
                        this.isClickedPostCommentDeleteButton = false;
                    });

                    subXhr.addEventListener("error", event => {
                        this.showSweetAlertErrorMessage("댓글 삭제 작업에 실패하였습니다.");
                        this.loadingStop(spinner2, "commentDeleteLoading");
                        this.isClickedPostCommentDeleteButton = false;
                    });

                    subXhr.send();
                }
            }
        });

        xhr.addEventListener("error", event => {
            this.showSweetAlertErrorMessage("댓글 삭제 권한 확인 작업에 실패하였습니다.");
            this.isClickedPostCommentDeleteButton = false;
        });

        xhr.send();
        this.isClickedPostCommentDeleteButton = true;
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

    #checkComment(commentId, commentCount) {
        const xhr = new XMLHttpRequest();

        xhr.open("GET", `/comment/exist/${commentId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = event.target.responseText;

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showSweetAlertErrorMessage(responseValue);
                location.reload();
            } else {
                if (responseValue === "true") {
                    this.#requestComment(`/comment/${this.postCommentPostId.value}/${this.postCommentBlogId.value}`, Math.ceil((commentCount / this.commentRecordSize)));
                } else {
                    location.reload();
                }
            }
        });

        xhr.addEventListener("error", event => {
            this.showSweetAlertErrorMessage("댓글 등록 여부 확인에 실패하였습니다.");
            location.reload();
        });

        xhr.send();
    }

    #requestComment(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.commentRecordSize, this.commentPageSize);
        const spinner = this.loadingSpin({
            lines: 15, length: 5, width: 5, radius: 8, scale: 1,
            corners: 1, color: '#000', opacity: 0.25, rotate: 0, direction: 1, speed: 1,
            trail: 60, fps: 20, zIndex: 2e9, className: 'spinner', top: '50%', left: '50%',
            shadow: false, hwaccel: false, position: 'absolute'
        }, "commentSearchLoading");

        xhr.open("GET", url + '?' + queryParam.toString(), true);


        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showSweetAlertErrorMessage(responseValue["message"]);
                this.loadingStop(spinner, "commentSearchLoading");
            } else {
                this.loadingStop(spinner, "commentSearchLoading");
                this.#handleTemplateList(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["commentPaginationResponse"]["commentPagination"], queryParam, url);
                // 게시글 상세 화면에서 url이 #href일 경우  특정 댓글 위치로 스크롤
                this.scrollTargetElement(this.getUrlStrAndParse('#'));
            }
            this.isClickedPostCommentSubmuitButton = false;
        });

        xhr.addEventListener("error", event => {
            this.showSweetAlertErrorMessage("댓글 정보를 불러오는데 실패하였습니다.");
            this.loadingStop(spinner, "commentSearchLoading");
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