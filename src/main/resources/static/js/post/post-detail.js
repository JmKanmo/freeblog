class PostDetailController extends UtilController {
    constructor() {
        super();
        this.postTitle = document.getElementById("blog_post_title");
        this.postTitleCategoryButton = document.getElementById("post_title_category_button");
        this.postLikeButton = document.getElementById("post_like_button");
        this.postLikeUserCheckButton = document.getElementById("post_like_user_check_button");
        this.post_share_button = document.getElementById("post_share_button");
        this.postUrlButton = document.getElementById("post_url_button");
        this.postContents = this.getReadOnlyQuillEditor('post_contents');

    }

    initPostDetailController() {
        this.initPostTitle();
        this.initEventController();
    }

    initPostTitle() {
        document.title = `게시글: ${this.postTitle.value}`;
    }

    initEventController() {
        this.postTitleCategoryButton.addEventListener("click", evt => {
            this.showToastMessage("post title category button clicked");
        });

        this.postLikeButton.addEventListener("click", evt => {
            this.showToastMessage("post like button clicked");
        });

        this.postLikeUserCheckButton.addEventListener("click", evt => {
            this.showToastMessage("post like user check button clicked");
        });

        this.post_share_button.addEventListener("click", evt => {
            this.showToastMessage("post share button clicked");
        });

        this.postUrlButton.addEventListener("click", evt => {
            this.copyUrl();
        });
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const postDetailController = new PostDetailController();
    postDetailController.initPostDetailController();
});