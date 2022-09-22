class HeaderController extends UtilController {
    constructor() {
        super();
        this.dayNightSkinButton = document.getElementById("day_night_skin_button");
        this.likePostButton = document.getElementById("like_post_button");
        this.userOptionDiv = document.getElementById("user_option_div");
        this.userProfileBtn = document.getElementById("user_profile_button");
        this.headerProfileImage = document.getElementById("header_profile_image");
        this.noticeButton = document.getElementById("notice_button");
    }

    initHeaderController() {
        if (this.dayNightSkinButton != null) {
            this.dayNightSkinButton.addEventListener("click", evt => {
                const img = evt.target.closest("img");
                if (img.src.includes('sun')) {
                    img.src = '../images/night.png'
                } else if (img.src.includes('night')) {
                    img.src = '../images/sun.gif';
                }
            });
        }

        if (this.likePostButton != null) {
            this.likePostButton.addEventListener("click", evt => {
                this.showToastMessage("좋아요 누른 글 버튼 기능 추가 예정");
            });
        }

        if (this.noticeButton != null) {
            this.noticeButton.addEventListener("click", evt => {
                this.showToastMessage("공지알림,리다이렉트 기능 추후 추가 예정");
            });
        }

        if (this.userProfileBtn != null) {
            this.userProfileBtn.addEventListener("click", evt => {
                if (this.userOptionDiv != null) {
                    if (this.userOptionDiv.style.display === '' || this.userOptionDiv.style.display === 'none') {
                        this.userOptionDiv.style.display = 'block';
                    } else {
                        this.userOptionDiv.style.display = 'none';
                    }
                }
            });
        }

        /**
         * 추후에 JS es6 코드로 대체 ...
         */
        $('body').click(function (e) {
            if (!$('#user_option_div').has(e.target).length) {
                const button = e.target.closest("button");
                if (button == null || button.className !== "user_profile_button") {
                    $('#user_option_div').hide();
                }
            }
        });
    }

    removeHeaderUserProfileImage() {
        this.headerProfileImage.src = this.getDefaultUserProfileThumbnail();
    }

    setHeaderUserProfileImage(src) {
        this.headerProfileImage.src = src;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // 추후에 로드 시에, 서버에서 전달받은 데이터 로드 메서드 호출
    const headerController = new HeaderController();
    headerController.initHeaderController();
});