class PostCommentCommonController extends UtilController {
    constructor() {
        super();
        this.postCommentImageButton = document.getElementById("post_comment_image_button");
        this.postCommentLockButton = document.getElementById("post_comment_lock_button");
        this.postCommentImageFileInput = document.getElementById("post_comment_image_file_input");
        this.postCommentImageForm = document.getElementById("post_comment_image_form");
        this.postCommentThumbnailImage = document.getElementById("post_comment_thumbnail_image");
        this.postCommentThumbnailImageBox = document.getElementById("post_comment_thumbnail_image_box");
        this.postCommentThumbnailImageDeleteButton = document.getElementById("post_comment_thumbnail_image_delete_button");
        this.postCommentSecretImage = document.getElementById("post_comment_secret_image");

        this.postCommentBlogId = document.getElementById("post_comment_blogId");
        this.postCommentPostId = document.getElementById("post_comment_postId");
        this.postCommentThumbnailImageValueInput = document.getElementById("post_comment_thumbnail_image_value_input");
        this.postCommentIsSecretInput = document.getElementById("post_comment_is_secret_input");
        this.commentIsAnonymous = document.getElementById("commentIsAnonymous");
        this.postCommentTextInput = document.getElementById("post_comment_text_input");
        this.commentUserNickname = document.getElementById("commentUserNickname");
        this.commentUserPassword = document.getElementById("commentUserPassword");
        this.postCommentMetaKey = document.getElementById("post_comment_metaKey");

        this.currentTextCount = document.getElementById("current_text_count");
        this.isImageUploadFlag = false;
    }

    initEventListener() {
        if (this.postCommentImageButton != null) {
            this.postCommentImageButton.addEventListener("click", evt => {
                this.postCommentImageFileInput.click();
            });
        }

        if (this.postCommentImageFileInput != null) {
            this.postCommentImageFileInput.addEventListener("change", evt => {
                if (this.isImageUploadFlag === true) {
                    this.showToastMessage("이미지 업로드를 진행 중입니다.");
                    return;
                }

                const imgFile = evt.target.files[0];

                if (this.checkImageFileExtension(imgFile, ['jpg', 'jpeg', 'png', 'gif', 'GIF'])) {
                    if (this.checkImageFileExtension(imgFile, ['gif', 'GIF']) && this.checkImageFileBySize(imgFile, this.MAX_THUMBNAIL_IMAGE_UPLOAD_SIZE)) {
                        // if file extension is gif | GIF, 300KB가 넘지 않는 경우, 압축 진행 X
                        this.#uploadImage(imgFile);
                    } else {
                        this.getCompressedImageFile(imgFile).then(compressedImgFile => {
                            this.#uploadImage(compressedImgFile);
                        });
                    }
                } else {
                    this.showToastMessage("지정 된 이미지 파일 ('jpg', 'jpeg', 'png', 'gif', 'GIF')만 업로드 가능합니다.");
                    this.isImageUploadFlag = false;
                }
            });
        }

        if (this.postCommentThumbnailImageDeleteButton != null) {
            this.postCommentThumbnailImageDeleteButton.addEventListener("click", evt => {
                if (confirm("해당 댓글 이미지를 삭제하겠습니까?")) {
                    this.removeCommentImage();
                }
            });
        }

        if (this.postCommentTextInput != null) {
            this.postCommentTextInput.addEventListener("input", evt => {
                this.setTextCount(evt.target);
            });
        }
    }

    #uploadImage(imgFile) {
        if (this.checkImageFile(imgFile)) {
            const fileReader = new FileReader();

            fileReader.onload = (event) => {
                const xhr = new XMLHttpRequest();
                const formData = new FormData(this.postCommentImageForm);

                xhr.open("POST", `/comment/upload/comment-thumbnail-image`, true);
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

                xhr.addEventListener("loadend", event => {
                    let status = event.target.status;
                    const responseValue = JSON.parse(event.target.responseText);

                    if ((status >= 400 && status <= 500) || (status > 500)) {
                        this.showToastMessage(responseValue["message"]);
                        this.removeCommentImage();
                    } else {
                        this.postCommentThumbnailImageBox.style.display = "block";
                        this.postCommentThumbnailImage.src = responseValue["imageSrc"];
                        this.postCommentThumbnailImageValueInput.value = responseValue["imageSrc"];
                        this.postCommentMetaKey.value = responseValue["metaKey"];
                    }
                    this.isImageUploadFlag = false;
                });

                xhr.addEventListener("error", event => {
                    this.showToastMessage('오류가 발생하여 댓글 이미지 전송에 실패하였습니다.');
                    this.removeCommentImage();
                    this.isImageUploadFlag = false;
                });
                formData.set("compressed_post_comment_image", imgFile);
                formData.set("uploadType", this.UPLOAD_IMAGE_TYPE);
                formData.set("uploadKey", !this.postCommentMetaKey.value ? new Date().getTime() : this.postCommentMetaKey.value);
                xhr.send(formData);
            }
            fileReader.readAsDataURL(imgFile);
            this.isImageUploadFlag = true;
        } else {
            this.removeCommentImage();
            this.isImageUploadFlag = false;
        }
    }

    setTextCount(commentTextArea) {
        const textLength = commentTextArea.value.length;
        this.currentTextCount.textContent = textLength;
    }

    removeCommentImage() {
        let fileBuffer = new DataTransfer();
        this.postCommentImageFileInput.files = fileBuffer.files; // <-- according to your file input reference
        this.postCommentThumbnailImage.src = "";
        this.postCommentThumbnailImageBox.style.display = "none";
        this.postCommentThumbnailImageValueInput.value = null;
        this.postCommentMetaKey.value = null;
    }
}