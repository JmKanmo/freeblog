class PostUpdateController extends UtilController {
    constructor() {
        super();
        this.postUpdateForm = document.getElementById("post_update_form");
        this.postUpdateResetButton = document.getElementById("post_update_reset_button");
        this.postTitle = document.getElementById("post_title");
        this.postWriterEditor = this.getQuillEditor('post_write_editor');
        this.viewSourceBtn = document.getElementById("view-editor-source");
        this.postCategory = document.getElementById("post_category");
        this.postTagInput = document.getElementById("post_tag_input");
        this.createdTagBox = document.getElementById("created_tag_box");
        this.tagRegisterButton = document.getElementById("tag_register_button");
        this.postThumbnailInputButton = document.getElementById("post_thumbnail_input_button");
        this.postThumbnailImageInput = document.getElementById("post_thumbnail_image_input");
        this.postThumbnailImage = document.getElementById("post_thumbnail_image");
        this.postThumbnailImageURL = null;
        this.postThumbnailImageForm = document.getElementById("post_thumbnail_image_form");
        this.postThumbnailImageBox = document.getElementById("post_thumbnail_image_box");
        this.postThumbnailImageDeleteButton = document.getElementById("post_thumbnail_image_delete_button");

        this.hiddenBlogPostThumbnailImage = document.getElementById("hidden_blog_post_thumbnail_image");
        this.hiddenTagTextList = document.getElementById("hidden_tag_text_list");
        this.hiddenBlogPostCategory = document.getElementById("hidden_blog_post_category");
        this.hiddenBlogPostContents = document.getElementById("hidden_blog_post_contents");
        this.hiddenBlogPostSummary = document.getElementById("hidden_blog_post_summary");
        this.hiddenBlogPostTitle = document.getElementById("hidden_blog_post_title");
        this.hiddenPostCategoryId = document.getElementById("hidden_post_category_id");
        this.hiddenMetaKey = document.getElementById("hidden_meta_key");
        this.isSubmitFlag = false;
        this.isImageUploadFlag = false;
        this.tagSet = new Set();
        this.musicHeaderController = new MusicHeaderController();
    }

    initPostUpdateController() {
        this.initPostContent();
        this.initElement();
        this.initEventListener();
        this.musicHeaderController.initMusicPlayer();
    }

    initPostContent() {
        this.postWriterEditor.root.innerHTML = this.compressContent(document.getElementById("hidden_post_content").value, false);
    }

    initElement() {
        // init tag set
        for (let i = 0; i < this.createdTagBox.children.length; i++) {
            this.tagSet.add(this.createdTagBox.children[i].innerText);
        }

        // init category template
        this.#initCategoryTemplate();

        // post thumbnail image
        if (this.postThumbnailImageBox.style.display !== 'none' && this.postThumbnailImageBox.style.display !== "") {
            this.postThumbnailImageURL = this.postThumbnailImage.src;
        }
    }

    #initCategoryTemplate() {
        const xhr = new XMLHttpRequest();
        const spinner = this.loadingSpin({
            lines: 15,
            length: 5,
            width: 5,
            radius: 5,
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
        }, "postUpdateLoading");
        const blogId = document.getElementById("hidden_blog_id").value;

        xhr.open("GET", `/category/all/${blogId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if ((status >= 400 && status <= 500) || (status > 500)) {
                this.showSweetAlertErrorMessage(responseValue["message"]);
                this.loadingStop(spinner, "postUpdateLoading");
            } else {
                const categorySelectorOptionTemplate = document.getElementById("category-selector-option-template").innerHTML;
                const categorySelectorOptionTemplateObject = Handlebars.compile(categorySelectorOptionTemplate);
                const categorySelectorOptionTemplateHTML = categorySelectorOptionTemplateObject(responseValue["categoryDto"]);
                this.postCategory.innerHTML = categorySelectorOptionTemplateHTML;
                this.postCategory.value = this.hiddenPostCategoryId.value;
                this.loadingStop(spinner, "postUpdateLoading");
            }
        });

        xhr.addEventListener("error", event => {
            this.showSweetAlertErrorMessage('오류가 발생하여 카테고리 데이터 수집에 실패하였습니다.');
            this.loadingStop(spinner, "postUpdateLoading");
        });
        xhr.send();
    }

    initEventListener() {
        this.viewSourceBtn.addEventListener("click", evt => {
            const wnd = this.openPopUp(500, 500, "HTML Source View", "new window");

            wnd.document.write(
                `<html>
                    <head>
                    <title>View HTML Source</title>
                    <style>
                        .code_view_title {
                            text-align:center;
                            color: #607d8b;
                            font-size: 18px;
                        }
                        
                         .code_container { 
                             width: 485px;
                             height: 455px;
                             text-align: left; 
                             white-space: pre-line; 
                             background-color: #f1f8e9; 
                             border: 1px solid #eeeeee;
                             color: #9c27b0; 
                             padding:10px;
                          }
                          
                         .code_container:before { 
                             content: ""; 
                             display: block; 
                             height: 1em; 
                             margin: 0 -5px -2em -5px; 
                        }
                    </style> 
                    </head>
                   
                    <body>                   
                        <h1 class="code_view_title">HTML Source View</h1>
                            <textarea class="code_container">
                               ${this.postWriterEditor.root.innerHTML}
                            </textarea>
                    </body>
                </html>`);
        });

        this.tagRegisterButton.addEventListener("click", evt => {
            const tagText = this.postTagInput.value;

            if (tagText.length <= 0 || /\s/g.test(tagText)) {
                this.showSweetAlertWarningMessage("비어있거나 공백이 포함 된 문자는 등록할 수 없습니다.");
                return;
            } else if (this.tagSet.has(`#${tagText}`)) {
                this.showSweetAlertWarningMessage("이미 등록한 태그는 등록할 수 없습니다.");
                return;
            }

            if (this.checkSpecialCharacter(tagText)) {
                this.showSweetAlertWarningMessage("특수문자는 태그로 등록할 수 없습니다.");
            } else {
                if (this.createdTagBox.children.length >= 30) {
                    this.showSweetAlertWarningMessage("태그는 최대 30개만 등록할 수 있습니다.");
                    return;
                }
                if (this.createdTagBox.children.length == 0) {
                    this.createdTagBox.style.display = 'block';
                }
                const spanTagText = document.createElement('span');
                spanTagText.className = 'created_tag_text';
                spanTagText.innerText = `#${tagText}`;
                this.createdTagBox.appendChild(spanTagText);
                this.tagSet.add(spanTagText.innerText);
                this.postTagInput.value = "";
            }
        });

        this.createdTagBox.addEventListener("click", evt => {
            const clickedTag = evt.target;

            if (clickedTag.className == "created_tag_text") {
                if (confirm('태그를 삭제하겠습니까?')) {
                    this.createdTagBox.removeChild(clickedTag);
                    this.tagSet.delete(clickedTag.innerText);

                    if (this.createdTagBox.children.length == 0) {
                        this.createdTagBox.style.display = 'none';
                    }
                }
            }
        });

        this.postThumbnailInputButton.addEventListener("click", evt => {
            this.postThumbnailImageInput.click();
        });

        this.postThumbnailImageInput.addEventListener("change", evt => {
            if (this.isImageUploadFlag === true) {
                this.showSweetAlertInfoMessage("이미지 업로드를 진행 중입니다.", 3000);
                return;
            }

            const imgFile = evt.target.files[0];

            if (this.checkImageFileExtension(imgFile, ['jpg', 'JPG', 'jpeg', 'JPEG', 'png', 'PNG', 'gif', 'GIF'])) {
                if (this.checkImageFileExtension(imgFile, ['gif', 'GIF']) && this.checkImageFileBySize(imgFile, this.MAX_THUMBNAIL_IMAGE_UPLOAD_SIZE)) {
                    // if file extension is gif | GIF, 300KB가 넘지 않는 경우, 압축 진행 X
                    this.#uploadImage(imgFile);
                } else {
                    this.getCompressedImageFile(imgFile).then(compressedImgFile => {
                        this.#uploadImage(compressedImgFile);
                    });
                }
            } else {
                this.isImageUploadFlag = false;
                this.showSweetAlertWarningMessage("지정 된 이미지 파일 ('jpg', 'JPG', 'jpeg', 'JPEG', 'png', 'PNG', 'gif', 'GIF')만 업로드 가능합니다.");
            }
        });

        this.postThumbnailImageDeleteButton.addEventListener("click", evt => {
            if (confirm("썸네일 이미지를 삭제하겠습니까?")) {
                this.removePostThumbnailImage();
            }
        });

        this.postUpdateForm.addEventListener("submit", evt => {
            if (this.isSubmitFlag === true) {
                this.showSweetAlertInfoMessage("게시글을 수정 중입니다.", 3000);
                return;
            }
            evt.preventDefault();

            if (confirm('게시글을 수정하겠습니까?')) {
                if (this.checkPostUpdateInfo()) {
                    this.showSweetAlertWarningMessage(`빈칸,공백만 포함 된 정보는 유효하지 않습니다. ${this.getCheckedPostUpdateInfoMessage()}`);
                    this.isSubmitFlag = false;
                    return false;
                } else {
                    const compressedContent = this.compressContent(this.postWriterEditor.root.innerHTML, true);

                    if (this.checkPostContentSize(compressedContent, this.MAX_POST_CONTENT_SIZE)) {
                        this.showSweetAlertWarningMessage("게시글 본문 크기가 허용 범위를 초과하였습니다.");
                        return;
                    }

                    this.hiddenBlogPostContents.value = compressedContent;
                    this.hiddenBlogPostSummary.value = this.replaceAndSubHTMlTag(this.postWriterEditor.root.innerHTML, 200);
                    this.hiddenBlogPostThumbnailImage.value = this.postThumbnailImageURL;
                    this.hiddenBlogPostCategory.value = this.postCategory.value;
                    this.hiddenBlogPostTitle.value = this.postTitle.value;
                    this.hiddenMetaKey.value = document.getElementById("upload_key").value;
                    this.setTagText();
                    this.postUpdateForm.submit();
                    this.isSubmitFlag = true;
                    return true;
                }
            }
        });

        this.postUpdateResetButton.addEventListener("click", evt => {
            if (confirm("수정 된 정보를 초기화 하겠습니까?")) {
                location.reload(true);
            }
        });
    }

    setTagText() {
        let tagList = '';

        for (let i = 0; i < this.createdTagBox.children.length; i++) {
            tagList += this.createdTagBox.children[i].innerText;

            if (i < this.createdTagBox.children.length - 1) {
                tagList += ',';
            }
        }
        this.hiddenTagTextList.value = tagList;
    }

    #uploadImage(imgFile) {
        if (this.checkImageFile(imgFile)) {
            const fileReader = new FileReader();

            fileReader.onload = (event) => {
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
                    top: '30%',
                    left: '50%',
                    shadow: false,
                    hwaccel: false,
                    position: 'absolute'
                }, "postUploadImageLoading");
                const formData = new FormData(this.postThumbnailImageForm);

                xhr.open("POST", `/post/upload/post-thumbnail-image`, true);
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

                xhr.addEventListener("loadend", event => {
                    let status = event.target.status;
                    const responseValue = JSON.parse(event.target.responseText);

                    if ((status >= 400 && status <= 500) || (status > 500)) {
                        this.showSweetAlertErrorMessage(responseValue["message"]);
                        this.loadingStop(spinner, "postUploadImageLoading");
                    } else {
                        // this.showToastMessage('게시글 썸네일 이미지가 지정되었습니다.');
                        this.postThumbnailImageBox.style.display = 'block';
                        this.postThumbnailImage.src = responseValue["imageSrc"];
                        document.getElementById("upload_key").value = responseValue["metaKey"];
                        this.postThumbnailImageURL = this.postThumbnailImage.src;
                        this.loadingStop(spinner, "postUploadImageLoading");
                    }
                    this.isImageUploadFlag = false;
                });

                xhr.addEventListener("error", event => {
                    this.showSweetAlertErrorMessage('오류가 발생하여 이미지 전송에 실패하였습니다.');
                    this.loadingStop(spinner, "postUploadImageLoading");
                });
                formData.set("compressed_post_image", imgFile);
                formData.set("uploadType", this.UPLOAD_IMAGE_TYPE);
                formData.set("uploadKey", !document.getElementById("upload_key").value ? new Date().getTime() : document.getElementById("upload_key").value);
                xhr.send(formData);
            }
            fileReader.readAsDataURL(imgFile);
            this.isImageUploadFlag = true;
        } else {
            this.isImageUploadFlag = false;
            this.removePostThumbnailImage();
        }
    }

    removePostThumbnailImage() {
        let fileBuffer = new DataTransfer();
        this.postThumbnailImage.src = "";
        this.postThumbnailImageURL = null;
        this.postThumbnailImageBox.style.display = 'none';
        this.postThumbnailImageInput.files = fileBuffer.files; // <-- according to your file input reference
    }

    checkPostUpdateInfo() {
        if (!this.postTitle.value || !this.postCategory.value ||
            ((this.postWriterEditor.root.innerText === null || this.getRemoveSpaceStr(this.postWriterEditor.root.innerHTML) === "<p></p>") ||
                (this.postWriterEditor.root.innerText.replace(/ /g, "") === null || this.getRemoveSpaceStr(this.postWriterEditor.root.innerHTML) === "<p></p>"))) {
            return true;
        } else {
            return false;
        }
    }

    getCheckedPostUpdateInfoMessage() {
        let msg = ``;

        if (!this.postTitle.value) {
            msg += "포스트 제목이 비어있습니다. ";
        }

        if (!this.postCategory.value) {
            msg += "카테고리가 비어있습니다. ";
        }

        if (((this.postWriterEditor.root.innerText === null || this.getRemoveSpaceStr(this.postWriterEditor.root.innerHTML) === "<p></p>") ||
            (this.postWriterEditor.root.innerText.replace(/ /g, "") === null || this.getRemoveSpaceStr(this.postWriterEditor.root.innerHTML) === "<p></p>"))) {
            msg += "게시글 본문이 빈칸,공백만 포함되어있습니다.";
        }
        return msg;
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const postUpdateController = new PostUpdateController();
    postUpdateController.initPostUpdateController();
});