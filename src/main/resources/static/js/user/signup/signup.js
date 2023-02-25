class SignUpController extends UtilController {
    constructor() {
        super();
        this.idCheckButton = document.getElementById("id-double_check_button");
        this.idInput = document.getElementById("user_id");
        this.emailCheckButton = document.getElementById("email-double_check_button");
        this.emailInput = document.getElementById("user_email");
        this.idCheckFlagInput = document.getElementById("id_check_flag");
        this.emailCheckFlagInput = document.getElementById("email_check_flag");
        this.greetingTextArea = document.getElementById("user_greeting_textarea");
        this.currentTextCount = document.getElementById("current_text_count");
        this.greetingLimitWarn = document.getElementById("greeting_limit_warn");

        this.userIntroButton = document.getElementById("user_intro_button");
        this.userIntroBlock = document.getElementById("user_intro_block");

        this.introEditor = this.getQuillEditor('signup_editor');
        this.viewSourceButton = document.getElementById("view-editor-source");
        this.userIntroEditorInput = document.getElementById("user_intro_editor_input");

        this.userSignUpForm = document.getElementById("user_signup_form");

        this.isSubmitFlag = false;
    }

    initSignUpController() {
        this.idCheckButton.addEventListener("click", evt => {
            const id = this.idInput.value;

            if (this.checkIdRegExp(id) === false) {
                this.showToastMessage('id 패턴에 적합하지 않습니다.');
                return;
            }

            this.idCheckButton.disabled = true;

            const xhr = new XMLHttpRequest();
            xhr.open("GET", `/user/check-id?id=${id}`, true);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(`${responseValue}`, false, 5000, () => {
                        this.idCheckButton.disabled = false;
                    });
                    this.idCheckFlagInput.value = false;
                } else {
                    this.showToastMessage(`${responseValue}`, false, 5000, () => {
                        this.idCheckButton.disabled = false;
                    });
                    this.idCheckFlagInput.value = true;
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('id 중복확인에 실패하였습니다.', false, 5000, () => {
                    this.idCheckButton.disabled = false;
                });
            });
            xhr.send();
        });

        this.greetingTextArea.addEventListener("input", evt => {
            this.setTextCount(evt.target);
        });

        this.emailCheckButton.addEventListener("click", evt => {
            const email = this.emailInput.value;

            if (this.checkEmailRegExp(email) === false) {
                this.showToastMessage('이메일 패턴에 적합하지 않습니다.');
                return;
            }

            this.emailCheckButton.disabled = true;

            const xhr = new XMLHttpRequest();
            xhr.open("GET", `/user/check-email?email=${email}`, true);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(`${responseValue}`, false, 5000, () => {
                        this.emailCheckButton.disabled = false;
                    });
                    this.emailCheckFlagInput.value = false;
                } else {
                    this.showToastMessage(`${responseValue}`, false, 5000, () => {
                        this.emailCheckButton.disabled = false;
                    });
                    this.emailCheckFlagInput.value = true;
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('이메일 중복확인에 실패하였습니다.', false, 5000, () => {
                    this.emailCheckButton.disabled = false;
                });
            });
            xhr.send();
        });

        this.userIntroButton.addEventListener("click", evt => {
            if (this.userIntroBlock.style.display == '' || this.userIntroBlock.style.display == 'none') {
                this.userIntroBlock.style.display = 'block';
            } else {
                this.userIntroBlock.style.display = 'none';
            }
        });

        this.viewSourceButton.addEventListener("click", evt => {
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

        this.userSignUpForm.addEventListener("submit", evt => {
            if (this.isSubmitFlag === true) {
                this.showToastMessage("회원가입을 진행 중입니다.");
                return;
            }

            evt.preventDefault();

            if (confirm('회원가입 폼을 제출하시겠습니까?')) {
                this.userIntroEditorInput.value = this.introEditor.root.innerHTML;
                this.userSignUpForm.submit();
                this.isSubmitFlag = true;
                return true;
            } else {
                return false;
            }
        });
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
}

document.addEventListener("DOMContentLoaded", () => {
    const signUpController = new SignUpController();
    signUpController.initSignUpController();
});