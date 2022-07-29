class SignUpController extends UtilController {
    constructor() {
        super();
        this.idCheckButton = document.getElementById("id-double_check_button");
        this.idInput = document.getElementById("user_id");
        this.emailCheckButton = document.getElementById("email-double_check_button");
        this.emailInput = document.getElementById("user_email");
        this.idCheckFlagInput = document.getElementById("id_check_flag");
        this.emailCheckFlagInput = document.getElementById("email_check_flag");
    }

    initSignUpController() {
        this.idCheckButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();
            const id = this.idInput.value;


            if (this.checkIdRegExp(id) === false) {
                this.showToastMessage('id 패턴에 적합하지 않습니다.');
                return;
            }

            xhr.open("GET", `/user/check_id?id=${id}`);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if (status >= 400 && status <= 500) {
                    this.showToastMessage(`${responseValue}`);
                    this.idCheckFlagInput.value = false;
                } else {
                    this.showToastMessage(`${responseValue}`);
                    this.idCheckFlagInput.value = true;
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('id 중복확인에 실패하였습니다.');
            });
            xhr.send();
        });

        this.emailCheckButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();
            const email = this.emailInput.value;


            if (this.checkEmailRegExp(email) === false) {
                this.showToastMessage('이메일 패턴에 적합하지 않습니다.');
                return;
            }

            xhr.open("GET", `/user/check_email?email=${email}`);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if (status >= 400 && status <= 500) {
                    this.showToastMessage(`${responseValue}`);
                    this.emailCheckFlagInput.value = false;
                } else {
                    this.showToastMessage(`${responseValue}`);
                    this.emailCheckFlagInput.value = true;
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('이메일 중복확인에 실패하였습니다.');
            });
            xhr.send();
        });
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const signUpController = new SignUpController();
    signUpController.initSignUpController();
});