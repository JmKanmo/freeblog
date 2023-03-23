class HeaderController extends UtilController {
    constructor() {
        super();
        this.dayNightSkinButton = document.getElementById("day_night_skin_button");
        this.likePostButton = document.getElementById("like_post_button");
        this.userOptionDiv = document.getElementById("user_option_div");
        this.userProfileBtn = document.getElementById("user_profile_button");
        this.headerProfileImage = document.getElementById("header_profile_image");
        this.noticeButton = document.getElementById("notice_button");
        this.userLikePostBlockCloseButton = document.getElementById("user_like_post_block_close_button");
        this.userLikePostContainer = document.getElementById("user_like_post_container");
        this.userLikePostTemplateHTML = null;
    }

    initHeaderController() {
        this.initEventHandler();
    }

    initEventHandler() {
        if (this.userLikePostBlockCloseButton != null) {
            this.userLikePostBlockCloseButton.addEventListener("click", evt => {
                if (this.userLikePostContainer != null) {
                    this.userLikePostContainer.style.display = "none";
                }
            });
        }

        if (this.dayNightSkinButton != null) {
            this.dayNightSkinButton.addEventListener("click", evt => {
                const img = evt.target.closest("img");
                if (img.src.includes('sun')) {
                    img.src = '/images/night.png'
                } else if (img.src.includes('night')) {
                    img.src = '/images/sun.gif';
                }
            });
        }

        if (this.likePostButton != null) {
            this.likePostButton.addEventListener("click", evt => {
                if (this.userLikePostContainer != null) {
                    const display = this.userLikePostContainer.style.display;

                    if (display === "" || display === "none") {
                        this.userLikePostContainer.style.display = "block";

                        if (!this.userLikePostTemplateHTML) {
                            this.#requestUserLikePostInfo();
                        } else {
                            this.userLikePostContainer.innerHTML = this.userLikePostTemplateHTML;
                        }
                    } else {
                        this.userLikePostContainer.style.display = "none";
                    }
                }
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

    #requestUserLikePostInfo() {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", "/like/post/user-list", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", evt => {
            const status = evt.target.status;
            const responseValue = JSON.parse(evt.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handlePostLikeTemplateList(responseValue);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 사용자가 좋아요 누른 게시글 정보를 불러오지 못했습니다.');
        });

        xhr.send();
    }

    removeHeaderUserProfileImage() {
        this.headerProfileImage.src = this.getDefaultUserProfileThumbnail();
    }

    setHeaderUserProfileImage(src) {
        this.headerProfileImage.src = src;
    }

    #handlePostLikeTemplateList(responseValue) {
        const userLikePostTemplate = document.getElementById("user-like-post-template").innerHTML;
        const userLikePostTemplateObject = Handlebars.compile(userLikePostTemplate);
        const userLikePostTemplateHTML = userLikePostTemplateObject({"userLikePostList": responseValue["userLikePostInnerList"]});
        this.userLikePostContainer.innerHTML = userLikePostTemplateHTML;
        this.userLikePostTemplateHTML = userLikePostTemplateHTML;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // 추후에 로드 시에, 서버에서 전달받은 데이터 로드 메서드 호출
    const headerController = new HeaderController();
    headerController.initHeaderController();
});