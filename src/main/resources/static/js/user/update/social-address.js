class SocialAddressInfoController extends UtilController {
    constructor() {
        super();
        this.socialAddressInfoForm = document.getElementById("social_address_info_form");

        // init input value
        this.prevSocialAddressValue = window.opener.document.getElementById("user_social_address").value;
        this.socialAddressInput = document.getElementById("user_social_address");
        this.socialAddressInput.value = this.prevSocialAddressValue;

        this.prevGithubValue = window.opener.document.getElementById("user_github_address").value;
        this.githubInput = document.getElementById("user_github_address");
        this.githubInput.value = this.prevGithubValue;

        this.prevTwitterValue = window.opener.document.getElementById("user_twitter_address").value;
        this.twitterInput = document.getElementById("user_twitter_address");
        this.twitterInput.value = this.prevTwitterValue;

        this.prevInstagramValue = window.opener.document.getElementById("user_instagram_address").value;
        this.instagramInput = document.getElementById("user_instagram_address");
        this.instagramInput.value = this.prevInstagramValue;

        this.userIdInput = document.getElementById("user_id_input");
        this.userIdInput.value = window.opener.document.getElementById("user_basic_info_id").value;
    }

    initSocialAddressInfoController() {
        this.socialAddressInfoForm.addEventListener("submit", evt => {
            evt.preventDefault();

            if (confirm('소셜 정보를 수정하겠습니까?') === true) {
                if (this.checkPrevChangeSocialInfos()) {
                    this.showToastMessage("변경 된 정보가 없습니다.");
                    return false;
                }
                this.socialAddressInfoForm.submit();
                return true;
            } else {
                return false;
            }
        });
    }

    checkPrevChangeSocialInfos() {
        if (this.socialAddressInput.value === this.prevSocialAddressValue &&
            this.githubInput.value === this.prevGithubValue &&
            this.twitterInput.value === this.prevTwitterValue &&
            this.instagramInput.value === this.prevInstagramValue) {
            return true;
        } else {
            return false;
        }
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const socialAddressInfoController = new SocialAddressInfoController();
    socialAddressInfoController.initSocialAddressInfoController();
});