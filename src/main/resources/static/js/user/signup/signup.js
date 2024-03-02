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
        this.userSignUpForm = document.getElementById("user_signup_form");
        this.isSubmitFlag = false;
    }

    initSignUpController() {
        this.idCheckButton.addEventListener("click", evt => {
            const id = this.idInput.value;

            if (this.checkIdRegExp(id) === false) {
                this.showSweetAlertWarningMessage('id 패턴에 적합하지 않습니다.');
                return;
            }

            this.idCheckButton.disabled = true;

            const xhr = new XMLHttpRequest();
            xhr.open("GET", `/user/check-id?id=${id}`, true);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showSweetAlertErrorMessage(`${responseValue}`, 3000, () => {
                        this.idCheckButton.disabled = false;
                    });
                    this.idCheckFlagInput.value = false;
                } else {
                    this.showSweetAlertInfoMessage(`${responseValue}`, 3000, () => {
                        this.idCheckButton.disabled = false;
                    });
                    this.idCheckFlagInput.value = true;
                }
            });

            xhr.addEventListener("error", event => {
                this.showSweetAlertErrorMessage('id 중복확인에 실패하였습니다.', 3000, () => {
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
                this.showSweetAlertWarningMessage('이메일 패턴에 적합하지 않습니다.');
                return;
            }

            this.emailCheckButton.disabled = true;

            const xhr = new XMLHttpRequest();
            xhr.open("GET", `/user/check-email?email=${email}`, true);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showSweetAlertErrorMessage(`${responseValue}`, 3000, () => {
                        this.emailCheckButton.disabled = false;
                    });
                    this.emailCheckFlagInput.value = false;
                } else {
                    this.showSweetAlertInfoMessage(`${responseValue}`, 3000, () => {
                        this.emailCheckButton.disabled = false;
                    });
                    this.emailCheckFlagInput.value = true;
                }
            });

            xhr.addEventListener("error", event => {
                this.showSweetAlertErrorMessage('이메일 중복확인에 실패하였습니다.', 3000, () => {
                    this.emailCheckButton.disabled = false;
                });
            });
            xhr.send();
        });

        this.userSignUpForm.addEventListener("submit", evt => {
            if (this.isSubmitFlag === true) {
                this.showSweetAlertInfoMessage("회원가입을 진행 중입니다.", 3000);
                return;
            }

            evt.preventDefault();

            if (confirm('회원가입 폼을 제출하시겠습니까?')) {
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