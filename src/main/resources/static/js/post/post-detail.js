class PostDetailController extends UtilController {
    constructor() {
        super();
        this.postTitle = document.getElementById("blog_post_title");
        this.postSearchKeywordInput = document.getElementById("postSearchKeywordInput");
        this.postTitleCategoryButton = document.getElementById("post_title_category_button");
        this.postLikeButton = document.getElementById("post_like_button");
        this.postLikeButtonImage = document.getElementById("post_like_button_image");
        this.postLikeUserCheckButton = document.getElementById("post_like_user_check_button");
        this.post_share_button = document.getElementById("post_share_button");
        this.postUrlButton = document.getElementById("post_url_button");
        this.postContents = this.getReadOnlyQuillEditor('post_contents');
        this.postCommentController = new PostCommentController();
        this.categoryPostBlock = document.getElementById("category_post_block");
        this.categoryPostBlockCloseButton = document.getElementById("category_post_block_close_button");
        this.categoryPostList = document.getElementById("category_post_list");
        this.categoryPostPagination = document.getElementById("categoryPostPagination");

        this.postRecordSize = 5;
        this.postPageSize = 5;

        this.likeRecordSize = 5;
        this.likePageSize = 5;

        this.prevCategoryPostBlock = false;
        this.userLikePostBlock = false;
        this.postLikeCountText = document.getElementById("post_like_count_text");
        this.postDetailDeleteButton = document.getElementById("post_detail_delete_button");
        this.postSearchButton = document.getElementById("postSearchButton");
        this.postSearchForm = document.getElementById("postSearchForm");
        this.postDeleteForm = document.getElementById("post_delete_form");
        this.postLikeUserListContainer = document.getElementById("post_like_user_list_container");
        this.postLikeUserListBlock = document.getElementById("post_like_user_list_block");
        this.postUserLikePagination = document.getElementById("postUserLikePagination");
        this.postUserLikeCloseButton = document.getElementById("user_post_like_close_button");
    }

    initPostDetailController() {
        this.initPostTitle();
        this.initEventController();
        this.postCommentController.requestComment();
        this.postCommentController.initEventListener();
    }

    initPostTitle() {
        document.title = `게시글: ${this.postTitle.value}`;
    }

    initEventController() {
        this.postTitleCategoryButton.addEventListener("click", evt => {
            if (this.categoryPostBlock.style.display === 'none' || this.categoryPostBlock.style.display === '') {
                this.categoryPostBlock.style.display = 'block';
                if (this.prevCategoryPostBlock === false) {
                    this.#requestCategoryPost(this.postTitleCategoryButton.value);
                    this.prevCategoryPostBlock = true;
                }
            } else {
                this.categoryPostBlock.style.display = 'none';
            }
        });

        this.categoryPostBlockCloseButton.addEventListener("click", evt => {
            if (this.categoryPostBlock.style.display === 'block') {
                this.categoryPostBlock.style.display = 'none';
            }
        });

        this.postLikeButton.addEventListener("click", evt => {
            this.#requestPostLike();
        });

        this.postLikeUserCheckButton.addEventListener("click", evt => {
            if (this.postLikeUserListContainer.style.display === '' || this.postLikeUserListContainer.style.display === 'none') {
                if (this.userLikePostBlock === false) {
                    this.#requestPostUserLike("/like/post/liked/" + document.getElementById("postIdInput").value);
                } else {
                    this.postLikeUserListContainer.style.display = 'block';
                }
            } else {
                this.postLikeUserListContainer.style.display = 'none';
            }
        });

        this.post_share_button.addEventListener("click", evt => {
            this.showToastMessage("post share button clicked");
        });

        this.postUrlButton.addEventListener("click", evt => {
            this.copyUrl();
        });

        this.categoryPostPagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestCategoryPost(url, page);
                }
            }
        });

        if (this.postDetailDeleteButton != null) {
            this.postDetailDeleteButton.addEventListener("click", evt => {
                if (confirm("게시글을 삭제하겠습니까?")) {
                    this.postDeleteForm.submit();
                }
            });
        }

        this.postSearchButton.addEventListener("click", evt => {
            this.postSearchKeywordInput.value = this.getRemoveSpaceStr(this.postSearchKeywordInput.value);

            if (this.postSearchKeywordInput.value === '') {
                this.showToastMessage("키워드를 한글자 이상 입력해주세요.");
                return;
            }
            this.postSearchForm.submit();
        });

        this.postUserLikePagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestPostUserLike(url, page);
                }
            }
        });

        this.postUserLikeCloseButton.addEventListener("click", evt => {
            this.postLikeUserListContainer.style.display = 'none';
        });
    }

    #requestPostLike() {
        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/like/post", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", evt => {
            const status = evt.target.status;
            const responseValue = JSON.parse(evt.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue["like"] === true) {
                    this.postLikeCountText.innerText = Number(this.postLikeCountText.innerText) + 1;
                    this.postLikeButtonImage.src = "../images/heart.png";
                } else {
                    this.postLikeCountText.innerText = Number(this.postLikeCountText.innerText) - 1;
                    this.postLikeButtonImage.src = "../images/empty_heart.png";
                }
                this.userLikePostBlock = false;
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 좋아요를 누르지 못했습니다.');
        });

        xhr.send(JSON.stringify({
            "postId": document.getElementById("postIdInput").value,
            "blogId": document.getElementById("postSearchBlogIdInput").value
        }));
    }

    #requestPostUserLike(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.likeRecordSize, this.likePageSize);
        xhr.open("GET", url, true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", evt => {
            const status = evt.target.status;
            const responseValue = JSON.parse(evt.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue["likePaginationResponse"]["likeDto"].length <= 0) {
                    this.showToastMessage("공감을 누른 사용자가 없습니다.");
                    return;
                }
                this.#handlePostLikeTemplateList(responseValue);
                this.#clearPostLikePagination();
                this.#handlePostLikePagination(responseValue["likePaginationResponse"]["likePagination"], queryParam, url);
                this.postLikeUserListContainer.style.display = 'block';
                this.userLikePostBlock = true;
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 공감을 누른 사용자 정보를 불러오지 못했습니다.');
        });

        xhr.send();
    }

    #requestCategoryPost(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.postRecordSize, this.postPageSize);

        xhr.open("GET", url + '?' + queryParam.toString(), true);

        xhr.addEventListener("loadend", evt => {
            const status = evt.target.status;
            const responseValue = JSON.parse(evt.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue["postPaginationResponse"]["postDto"]["postSummaryDto"]["count"] <= 0) {
                    this.showToastMessage("게시글 정보가 존재하지 않습니다.");
                    return;
                }
                this.#handleCategoryPostTemplateList(responseValue);
                this.#clearCategoryPostPagination();
                this.#handleCategoryPostPagination(responseValue["postPaginationResponse"]["postPagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 게시글 정보를 불러오지 못했습니다.');
        });

        xhr.send();
    }

    #handleCategoryPostTemplateList(responseValue) {
        const categoryPostTemplate = document.getElementById("category-post-template").innerHTML;
        const categoryPostTemplateObject = Handlebars.compile(categoryPostTemplate);
        const categoryPostTemplateHTML = categoryPostTemplateObject(responseValue["postPaginationResponse"]["postDto"]);
        this.categoryPostList.innerHTML = categoryPostTemplateHTML;
    }

    #clearCategoryPostPagination() {
        this.categoryPostPagination.innerHTML = ``;
    }

    #handleCategoryPostPagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.categoryPostPagination.innerHTML = '';
            return;
        }
        this.categoryPostPagination.innerHTML = this.drawSimplePagination(pagination, queryParam, url);
    }

    #handlePostLikeTemplateList(responseValue) {
        const postLikeTemplate = document.getElementById("like-user-post-template").innerHTML;
        const postLikeTemplateObject = Handlebars.compile(postLikeTemplate);
        const postLikeTemplateHTML = postLikeTemplateObject({"userPostLikeList": responseValue["likePaginationResponse"]["likeDto"]});
        this.postLikeUserListBlock.innerHTML = postLikeTemplateHTML;
    }

    #clearPostLikePagination() {
        this.postUserLikePagination.innerHTML = ``;
    }

    #handlePostLikePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.postUserLikePagination.innerHTML = ``;
            return;
        }
        this.postUserLikePagination.innerHTML = this.drawSimplePagination(pagination, queryParam, url);
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const postDetailController = new PostDetailController();
    postDetailController.initPostDetailController();
});