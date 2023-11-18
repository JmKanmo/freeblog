class PostWriteController extends UtilController {
    constructor() {
        super();
        this.postWriteRegisterButton = document.getElementById("post_write_register_button");
        this.postWriteForm = document.getElementById("post_write_form");
        this.postTitle = document.getElementById("post_title");
        this.postWriterEditor = this.getQuillEditor('post_write_editor');
        this.viewSourceBtn = document.getElementById("view-editor-source");
        this.postTagInput = document.getElementById("post_tag_input");
        this.createdTagBox = document.getElementById("created_tag_box");
        this.postThumbnailInputButton = document.getElementById("post_thumbnail_input_button");
        this.postThumbnailImageInput = document.getElementById("post_thumbnail_image_input");
        this.postThumbnailImage = document.getElementById("post_thumbnail_image");
        this.postThumbnailImageURL = null;
        this.tagRegisterButton = document.getElementById("tag_register_button");
        this.postThumbnailImageForm = document.getElementById("post_thumbnail_image_form");
        this.postThumbnailImageBox = document.getElementById("post_thumbnail_image_box");
        this.postThumbnailImageDeleteButton = document.getElementById("post_thumbnail_image_delete_button");
        this.postCategory = document.getElementById("post_category");

        this.hiddenUserId = document.getElementById("hidden_user_id");
        this.hiddenBlogPostThumbnailImage = document.getElementById("hidden_blog_post_thumbnail_image");
        this.hiddenTagTextList = document.getElementById("hidden_tag_text_list");
        this.hiddenBlogPostCategory = document.getElementById("hidden_blog_post_category");
        this.hiddenBlogPostContents = document.getElementById("hidden_blog_post_contents");
        this.hiddenBlogPostSummary = document.getElementById("hidden_blog_post_summary");
        this.imageUploadFlag = false;
        this.isSubmitFlag = false;
        this.tagSet = new Set();
    }

    initPostWriteController() {
        this.#initTemplate();
        this.initEventListener();
        this.#initIntervalAndAutoSave();
    }

    #initTemplate() {
        const xhr = new XMLHttpRequest();
        const blogId = document.getElementById("hidden_blog_id").value;

        xhr.open("GET", `/category/all/${blogId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if ((status >= 400 && status <= 500) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                const categorySelectorOptionTemplate = document.getElementById("category-selector-option-template").innerHTML;
                const categorySelectorOptionTemplateObject = Handlebars.compile(categorySelectorOptionTemplate);
                const categorySelectorOptionTemplateHTML = categorySelectorOptionTemplateObject(responseValue["categoryDto"]);
                this.postCategory.innerHTML = categorySelectorOptionTemplateHTML;
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 카테고리 데이터 수집에 실패하였습니다.');
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

        // 스페이스바 태그 등록 (당장 필요가 없으니 주석 처리)
        // this.postTagInput.addEventListener("keydown", evt => {
        //     if (evt.keyCode == 32) {
        //         const tagText = evt.target.value;
        //
        //         if (tagText.length <= 0 || /\s/g.test(tagText)) {
        //             this.showToastMessage("비어있거나 공백이 포함 된 문자는 등록할 수 없습니다.");
        //             return;
        //         }
        //
        //         if (this.checkSpecialCharacter(tagText)) {
        //             this.showToastMessage("특수문자는 태그로 등록할 수 없습니다.");
        //         } else {
        //             if (this.createdTagBox.children.length == 0) {
        //                 this.createdTagBox.style.display = 'block';
        //             }
        //             const spanTagText = document.createElement('span');
        //             spanTagText.className = 'created_tag_text';
        //             spanTagText.innerText = `#${tagText}`;
        //             this.createdTagBox.appendChild(spanTagText);
        //             evt.target.value = "";
        //         }
        //     }
        // });

        this.tagRegisterButton.addEventListener("click", evt => {
            const tagText = this.postTagInput.value;

            if (tagText.length <= 0 || /\s/g.test(tagText)) {
                this.showToastMessage("비어있거나 공백이 포함 된 문자는 등록할 수 없습니다.");
                return;
            } else if (this.tagSet.has(`#${tagText}`)) {
                this.showToastMessage("이미 등록한 태그는 등록할 수 없습니다.");
                return;
            }

            if (this.checkSpecialCharacter(tagText)) {
                this.showToastMessage("특수문자는 태그로 등록할 수 없습니다.");
            } else {
                if (this.createdTagBox.children.length >= 30) {
                    this.showToastMessage("태그는 최대 30개만 등록할 수 있습니다.");
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
            if (this.imageUploadFlag === true) {
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
                this.imageUploadFlag = false;
            }
        });

        this.postThumbnailImageDeleteButton.addEventListener("click", evt => {
            if (confirm("썸네일 이미지를 삭제하겠습니까?")) {
                this.removePostThumbnailImage();
            }
        });

        this.postWriteRegisterButton.addEventListener("click", evt => {
            if (this.isSubmitFlag === true) {
                this.showToastMessage("게시글을 발행 중입니다.");
                return;
            }

            if (confirm('게시글을 발행하겟습니까?')) {
                if (this.checkPostSubmitInfo()) {
                    this.showToastMessage("빈칸,공백만 포함 된 정보는 유효하지 않습니다.");
                } else {
                    const compressedContent = this.compressContent(this.postWriterEditor.root.innerHTML, true);

                    if (this.checkPostContentSize(compressedContent, this.MAX_POST_CONTENT_SIZE)) {
                        this.showToastMessage("게시글 본문 크기가 허용 범위를 초과하였습니다.");
                        return;
                    }

                    this.isSubmitFlag = true;
                    this.hiddenBlogPostContents.value = compressedContent;
                    this.hiddenBlogPostSummary.value = this.replaceAndSubHTMlTag(this.postWriterEditor.root.innerHTML, 200);
                    this.hiddenBlogPostThumbnailImage.value = this.postThumbnailImageURL;
                    this.hiddenBlogPostCategory.value = this.postCategory.value;
                    this.setTagText();
                    this.clearInterval(this.postInterval, "postSaveInfo");
                    this.#uploadPost();
                }
            }
        });
    }

    #uploadPost() {
        const xhr = new XMLHttpRequest();
        const formData = new FormData(this.postWriteForm);

        xhr.open("POST", `/post/write/${this.hiddenUserId.value}`, true);
        xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if ((status >= 400 && status <= 500) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                const postId = responseValue["postDto"]["id"];
                const blogId = responseValue["postDto"]["blogId"];
                window.location.href = `/post/${postId}?blogId=${blogId}`;
            }
            this.isSubmitFlag = false;
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 게시글 업로드에 실패하였습니다.');
        });
        xhr.send(formData);
    }

    #uploadImage(imgFile) {
        if (this.checkImageFile(imgFile)) {
            const fileReader = new FileReader();

            fileReader.onload = (event) => {
                const xhr = new XMLHttpRequest();
                const formData = new FormData(this.postThumbnailImageForm);

                xhr.open("POST", `/post/upload/post-thumbnail-image`, true);
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

                xhr.addEventListener("loadend", event => {
                    let status = event.target.status;
                    const responseValue = event.target.responseText;

                    if ((status >= 400 && status <= 500) || (status > 500)) {
                        this.showToastMessage(responseValue);
                    } else {
                        this.showToastMessage('게시글 썸네일 이미지가 지정되었습니다.');
                        this.postThumbnailImageBox.style.display = 'block';
                        this.postThumbnailImage.src = responseValue;
                        this.postThumbnailImageURL = this.postThumbnailImage.src;
                    }
                    this.imageUploadFlag = false;
                });

                xhr.addEventListener("error", event => {
                    this.showToastMessage('오류가 발생하여 이미지 전송에 실패하였습니다.');
                    this.imageUploadFlag = false;
                });
                formData.set("compressed_post_image", imgFile);
                xhr.send(formData);
            }
            fileReader.readAsDataURL(imgFile);
            this.imageUploadFlag = true;
        } else {
            this.imageUploadFlag = false;
            this.removePostThumbnailImage();
        }
    }

    #initIntervalAndAutoSave() {
        // localStorage 정보 반환 및 복구
        this.setAutoSaveWriteInfo(JSON.parse(localStorage.getItem("postSaveInfo")));
        // interval 실행
        this.postInterval = this.invokeAutoSaveInterval(() => {
            const jsonObj = {
                title: this.postTitle.value == null ? "" : this.postTitle.value,
                category: this.postCategory.value == null ? "" : this.postCategory.value,
                tagInput: this.postTagInput.value == null ? "" : this.postTagInput.value,
                tagSet: this.tagSet.size > 0 ? JSON.stringify(this.tagSet, (_key, value) => (value instanceof Set ? [...value] : value)) : this.tagSet,
                contents: this.postWriterEditor.root.innerHTML == null ? "" : this.postWriterEditor.root.innerHTML,
                thumbnailImage: this.postThumbnailImageURL,
                uploadKey: document.getElementById("upload_key").value
            };
            localStorage.setItem("postSaveInfo", JSON.stringify(jsonObj));
        }, null, 1000 * 3);
    }

    setAutoSaveWriteInfo(autoSaveWriteInfo) {
        if (autoSaveWriteInfo != null) {
            this.postTitle.value = autoSaveWriteInfo["title"];
            this.postCategory.value = autoSaveWriteInfo["category"];
            this.postTagInput.value = autoSaveWriteInfo["tagInput"];
            this.tagSet = (typeof autoSaveWriteInfo["tagSet"]) === "object" ? this.tagSet : new Set(JSON.parse(autoSaveWriteInfo["tagSet"]));
            for (const tagSpan of this.tagSet) {
                this.createdTagBox.style.display = 'block';
                const spanTagText = document.createElement('span');
                spanTagText.className = 'created_tag_text';
                spanTagText.innerText = tagSpan;
                this.createdTagBox.appendChild(spanTagText);
            }
            this.postWriterEditor.root.innerHTML = autoSaveWriteInfo["contents"];
            if (autoSaveWriteInfo["thumbnailImage"] != null) {
                this.postThumbnailImageBox.style.display = 'block';
                this.postThumbnailImage.src = autoSaveWriteInfo["thumbnailImage"];
                this.postThumbnailImageURL = this.postThumbnailImage.src;
            }
            document.getElementById("upload_key").value = autoSaveWriteInfo["uploadKey"];
        }
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

    removePostThumbnailImage() {
        let fileBuffer = new DataTransfer();
        this.postThumbnailImage.src = "";
        this.postThumbnailImageURL = null;
        this.postThumbnailImageBox.style.display = 'none';
        this.postThumbnailImageInput.files = fileBuffer.files; // <-- according to your file input reference
    }

    checkPostSubmitInfo() {
        if (!this.postTitle.value || !this.postCategory.value ||
            ((this.postWriterEditor.root.innerText === null || this.getRemoveSpaceStr(this.postWriterEditor.root.innerHTML) === "<p></p>") ||
                (this.postWriterEditor.root.innerText.replace(/ /g, "") === null || this.getRemoveSpaceStr(this.postWriterEditor.root.innerHTML) === "<p></p>")) ||
            (!this.postWriterEditor.root.innerText.replace(/ /g, "") === null || this.getRemoveSpaceStr(this.postWriterEditor.root.innerHTML) === "<p></p>")) {
            return true;
        } else {
            return false;
        }
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const postWriteController = new PostWriteController();
    postWriteController.initPostWriteController();
});