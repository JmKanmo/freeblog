// Execute all functions

class SignUpCompleteController extends UtilController {
    constructor() {
        super();
        this.resendButton = document.getElementById("email_resend_button");
    }

    initSignUpCompleteController() {
        this.resendButtonInit();
    }

    resendButtonInit() {
        this.resendButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();
            const email = this.resendButton.value;

            xhr.open("GET", `/email/send/signup?email=${email}`);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if ((status >= 400 && status <= 500) || (status > 500)) {
                    this.showToastMessage(responseValue);
                } else {
                    this.showToastMessage(`이메일이 정상적으로 전송되었습니다.`);
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('이메일 전송에 실패하였습니다.');
            });
            xhr.send();
        });
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const signUpController = new SignUpCompleteController();
    signUpController.initSignUpCompleteController();
});