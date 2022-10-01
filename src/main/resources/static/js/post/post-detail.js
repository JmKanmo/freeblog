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
        this.postCommentImageButton = document.getElementById("post_comment_image_button");
        this.postCommentLockButton = document.getElementById("post_comment_lock_button");
        this.postCommentSubmitButton = document.getElementById("post_comment_submit_button");
        this.postCommentImageFileInput = document.getElementById("post_comment_image_file_input");
        this.postCommentImageForm = document.getElementById("post_comment_image_form");
        this.postCommentThumbnailImage = document.getElementById("post_comment_thumbnail_image");
        this.postCommentThumbnailImageBox = document.getElementById("post_comment_thumbnail_image_box");
        this.postCommentThumbnailImageDeleteButton = document.getElementById("post_comment_thumbnail_image_delete_button");
        this.postCommentSecretImage = document.getElementById("post_comment_secret_image");

        this.postCommentThumbnailImageValueInput = document.getElementById("post_comment_thumbnail_image_value_input");
        this.postCommentIsSecretInput = document.getElementById("post_comment_is_secret_input");
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

        this.postCommentImageButton.addEventListener("click", evt => {
            this.postCommentImageFileInput.click();
        });

        this.postCommentLockButton.addEventListener("click", evt => {
            const isCommentLock = this.postCommentIsSecretInput.value;

            if (isCommentLock === "true") {
                this.postCommentSecretImage.src = "../images/unlock.png";
                this.postCommentIsSecretInput.value = false;
            } else {
                this.postCommentSecretImage.src = "../images/lock.png";
                this.postCommentIsSecretInput.value = true;
            }
        });

        this.postCommentSubmitButton.addEventListener("click", evt => {
            this.showToastMessage("postCommentSubmitButton clicked");
        });

        this.postCommentImageFileInput.addEventListener("change", evt => {
            const imgFile = evt.target.files;

            if (this.checkProfileThumbnailFile(imgFile)) {
                const fileReader = new FileReader();

                fileReader.onload = (event) => {
                    const xhr = new XMLHttpRequest();

                    xhr.open("POST", `/comment/upload/comment-thumbnail-image`);

                    xhr.addEventListener("loadend", event => {
                        let status = event.target.status;
                        const responseValue = event.target.responseText;

                        if ((status >= 400 && status <= 500) || (status > 500)) {
                            this.showToastMessage(responseValue);
                            this.removeCommentImage();
                        } else {
                            this.postCommentThumbnailImageBox.style.display = "block";
                            this.postCommentThumbnailImage.src = responseValue;
                            this.postCommentThumbnailImageValueInput.value = responseValue;
                        }
                    });

                    xhr.addEventListener("error", event => {
                        this.showToastMessage('오류가 발생하여 댓글 이미지 전송에 실패하였습니다.');
                        this.removeCommentImage();
                    });
                    xhr.send(new FormData(this.postCommentImageForm));
                }
                fileReader.readAsDataURL(imgFile[0]);
            } else {
                this.removeCommentImage();
            }
        });

        this.postCommentThumbnailImageDeleteButton.addEventListener("click", evt => {
            if (confirm("해당 댓글 이미지를 삭제하겠습니까?")) {
                this.removeCommentImage();
            }
        });
    }

    removeCommentImage() {
        let fileBuffer = new DataTransfer();
        this.postCommentImageFileInput.files = fileBuffer.files; // <-- according to your file input reference
        this.postCommentThumbnailImage.src = "";
        this.postCommentThumbnailImageBox.style.display = "none";
        this.postCommentThumbnailImageValueInput.value = null;
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const postDetailController = new PostDetailController();
    postDetailController.initPostDetailController();
});