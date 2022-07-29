class FindInfoController extends UtilController {
    constructor() {
        super();
        this.nickNameInput = document.getElementById("user_nickname_input");
        this.emailFindButton = document.getElementById("email_find_button");
        this.passwordFindButton = document.getElementById("find_password_button");
        this.emailInput = document.getElementById("user_email");
        this.emailNotAuthButton = document.getElementById("email_not_auth_button");
        this.authEmailDiv = document.getElementById("auth_email_div");
        this.authTargetEmailInput = document.getElementById("auth_target_email_input");
        this.authTargetEmailButton = document.getElementById("auth_target_email_button");
    }

    initFindInfoController() {
        // email find button event listener
        this.emailFindButton.addEventListener("click", evt => {
            const nickname = this.nickNameInput.value;

            if (!nickname) {
                this.showToastMessage("닉네임 입력폼이 비어있습니다.");
                return;
            }

            const width = 500;
            const height = 500;

            let left = (document.body.offsetWidth / 2) - (width / 2);
            let tops = (document.body.offsetHeight / 2) - (height / 2);

            left += window.screenLeft;

            window.open(`find_email?nickname=${nickname}`, 'popup', `width=${width}, height=${height}, left=${left}, top=${tops}`);
        });

        // password find button event listener
        this.passwordFindButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();
            const email = this.emailInput.value;


            if (!email) {
                this.showToastMessage("이메일 입력폼이 비어있습니다.");
                return;
            }

            xhr.open("GET", `/email/send/find_password?email=${email}`);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if (status >= 400 && status <= 500) {
                    this.showToastMessage(`${responseValue}`);
                } else {
                    this.showToastMessage(`이메일이 정상적으로 전송되었습니다.`);
                    this.passwordFindButton.disabled = true;
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('이메일 전송에 실패하였습니다.');
            });
            xhr.send();
        });

        // email not auth button set event listener
        this.emailNotAuthButton.addEventListener("click", evt => {
            if (this.authEmailDiv.style.display == '' || this.authEmailDiv.style.display == 'none') {
                this.authEmailDiv.style.display = 'flex';
            } else {
                this.authEmailDiv.style.display = 'none';
            }
        });

        // email target auth button set event listener
        this.authTargetEmailButton.addEventListener("click", evt => {
            const xhr = new XMLHttpRequest();
            const email = this.authTargetEmailInput.value;

            if (!email) {
                this.showToastMessage("이메일 입력폼이 비어있습니다.");
                return;
            }

            xhr.open("GET", `/email/send/auth?email=${email}`);

            xhr.addEventListener("loadend", event => {
                let status = event.target.status;
                const responseValue = event.target.responseText;

                if (status >= 400 && status <= 500) {
                    this.showToastMessage(`${responseValue}`);
                } else {
                    this.showToastMessage(`이메일이 정상적으로 전송되었습니다.`);
                    this.authTargetEmailButton.disabled = true;
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('이메일 전송에 실패하였습니다.');
            });
            xhr.send();
        });
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const findInfoController = new FindInfoController();
    findInfoController.initFindInfoController();
});