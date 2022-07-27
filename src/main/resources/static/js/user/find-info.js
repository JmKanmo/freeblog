class FindInfoController extends UtilController {
    constructor() {
        super();
        this.nickNameInput = document.getElementById("user_nickname_input");
        this.emailFindButton = document.getElementById("email_find_button");
    }

    initFindInfoController() {
        this.emailFindButton.addEventListener("click", evt => {
            const nickname = this.nickNameInput.value;

            if (!nickname) {
                this.showToastMessage("닉네임 입력폼이 비어있습니다.");
                return;
            }

            const width = 600;
            const height = 600;

            let left = (document.body.offsetWidth / 2) - (width / 2);
            let tops = (document.body.offsetHeight / 2) - (height / 2);

            left += window.screenLeft;

            window.open(`find-email?nickname=${nickname}`, 'popup', `width=${width}, height=${height}, left=${left}, top=${tops}`);
        });
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const findInfoController = new FindInfoController();
    findInfoController.initFindInfoController();
});