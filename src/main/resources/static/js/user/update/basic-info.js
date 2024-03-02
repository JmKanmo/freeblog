class BasicInfoController extends UtilController {
    constructor() {
        super();
        this.userBasicInfoForm = document.getElementById("user_basic_info_form");

        this.greetingTextArea = document.getElementById("user_greeting_textarea");
        this.currentTextCount = document.getElementById("current_text_count");
        this.greetingLimitWarn = document.getElementById("greeting_limit_warn");

        this.introEditor = this.getQuillEditor('basic_info_editor');

        this.viewSourceBtn = document.getElementById("view-editor-source");
        this.userIntroEditorInput = document.getElementById("user_intro_editor_input")

        this.prevUserBasicInfoBlogName = window.opener.document.getElementById("user_basic_info_blog_name").value;
        this.prevUserBasicInfoNickName = window.opener.document.getElementById("user_basic_info_nickname").value;
        this.prevUserBasicInfoGreetings = window.opener.document.getElementById("user_basic_info_greetings").value;
        this.prevUserBasicInfoIntro = window.opener.document.getElementById("user_info_intro").value;

        this.isSubmitFlag = false;
    }

    initBasicInfoController() {
        this.initInputValue();
        this.initEventListener();
    }

    initInputValue() {
        document.getElementById("user_blog_name").value = window.opener.document.getElementById("user_basic_info_blog_name").value;
        document.getElementById("user_nickname").value = window.opener.document.getElementById("user_basic_info_nickname").value;
        document.getElementById("user_greeting_textarea").value = window.opener.document.getElementById("user_basic_info_greetings").value;
        this.setTextCount(this.greetingTextArea);
        this.introEditor.root.innerHTML = this.compressContent(window.opener.document.getElementById("user_info_intro").value, false);
        document.getElementById("user_id").value = window.opener.document.getElementById("user_basic_info_id").value;
    }

    setTextCount(greetingTextArea) {
        const textLength = greetingTextArea.value.length;

        if (textLength >= greetingTextArea.getAttribute("maxlength")) {
            this.greetingLimitWarn.style.visibility = 'visible';
        } else {
            this.greetingLimitWarn.style.visibility = 'hidden';
        }
        this.currentTextCount.textContent = textLength
    }

    initEventListener() {
        this.greetingTextArea.addEventListener("input", evt => {
            this.setTextCount(evt.target);
        });

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
                               ${this.introEditor.root.innerHTML}
                            </textarea>
                    </body>
                </html>`);
        });

        document.getElementById("user_basic_info_form").addEventListener("submit", evt => {
            if (this.isSubmitFlag === true) {
                this.showSweetAlertInfoMessage("개인정보를 수정 중입니다.", 3000);
                return;
            }

            evt.preventDefault();

            if (confirm('기본 정보를 수정하겠습니까?') === true) {
                const compressedContent = this.compressContent(this.introEditor.root.innerHTML, true);

                if (this.checkPostContentSize(compressedContent, this.MAX_INTRO_CONTENT_SIZE)) {
                    this.showSweetAlertWarningMessage("소개글 본문 크기가 허용 범위를 초과하였습니다.");
                    return;
                }

                this.userIntroEditorInput.value = compressedContent;

                if (this.checkPrevChangeUserInfos()) {
                    this.showSweetAlertInfoMessage("변경 된 정보가 없습니다.", 3000);
                    this.isSubmitFlag = false;
                    return false;
                }
                this.userBasicInfoForm.submit();
                this.isSubmitFlag = true;
                return true;
            } else {
                return false;
            }
        });
    }

    checkPrevChangeUserInfos() {
        if (document.getElementById("user_blog_name").value === this.prevUserBasicInfoBlogName &&
            document.getElementById("user_nickname").value === this.prevUserBasicInfoNickName &&
            document.getElementById("user_greeting_textarea").value === this.prevUserBasicInfoGreetings &&
            this.introEditor.root.innerHTML === this.prevUserBasicInfoIntro) {
            return true;
        } else {
            return false;
        }
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const basicInfoController = new BasicInfoController();
    basicInfoController.initBasicInfoController();
});