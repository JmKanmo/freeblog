class BlogHeaderController extends BlogBodyController {
    constructor() {
        super();
        this.recentPostTitle = document.getElementById("recent_post_title");
        this.popularPostTitle = document.getElementById("popular_post_title");
        this.postSearchKeywordInput = document.getElementById("postSearchKeywordInput");
        this.audioPlayer = this.initAudioPlayer(); // TODO
        this.introButton = document.getElementById("intro_button");

        this.blogHeaderCategoryList = document.getElementById("blog_header_category_list");
        this.clickedCategoryButton = null;
        this.postSearchButton = document.getElementById("postSearchButton");
        this.postSearchForm = document.getElementById("postSearchForm");
        this.categorySettingButton = document.getElementById("category_setting_button");

        this.recentPostCardList = document.getElementById("recent_post_card_list");
        this.popularPostCardList = document.getElementById("popular_post_card_list");

        this.audioPlayerCategoryList = document.getElementById("audioPlayerCategoryList");
        this.audioPlayerSettingButton = document.getElementById("audioPlayerSettingButton");
        this.musicLiveSettingButton = document.getElementById("musicLiveSettingButton");
    }

    initBlogHeaderController() {
        this.initDefault();
        this.initCalendar();
        this.initBlogHeaderEventListener();
    }

    initDefault() {
        this.recentPostTitle.style.color = '#333';
        this.recentPostCardList.style.display = 'block';
        this.#requestRecentPostCard();
        this.#requestPopularPostCard();
    }

    initBlogHeaderEventListener() {
        this.recentPostTitle.addEventListener("click", evt => {
            this.recentPostTitle.style.color = '#333';
            this.recentPostCardList.style.display = 'block';
            this.popularPostTitle.style.color = '#777';
            this.popularPostCardList.style.display = 'none';
        });

        this.popularPostTitle.addEventListener("click", evt => {
            this.popularPostTitle.style.color = '#333';
            this.popularPostCardList.style.display = 'block';
            this.recentPostTitle.style.color = '#777';
            this.recentPostCardList.style.display = 'none';
        });

        this.introButton.addEventListener("click", evt => {
            this.openPopUp(988, 750, '/user/intro', 'popup');
        });

        // category button event listener
        this.blogHeaderCategoryList.addEventListener("click", evt => {
            const clickedCategoryButton = evt.target.closest(".common_button_text");

            if (clickedCategoryButton != null) {
                if (localStorage.getItem("prevClickedValue") != clickedCategoryButton.value) {
                    if (this.clickedCategoryButton != null) {
                        this.clickedCategoryButton.style.fontWeight = "normal";
                        this.clickedCategoryButton.style.textDecoration = "none";
                    }
                    clickedCategoryButton.style.fontWeight = "bold";
                    clickedCategoryButton.style.textDecoration = "underline";

                    if (evt.target.id === 'total_category_search_title') {
                        this.requestAllBlogPost(clickedCategoryButton.value + '/' + document.getElementById("blog_info_id").value);
                    } else {
                        this.requestAllBlogPost(clickedCategoryButton.value);
                    }
                    localStorage.setItem("prevClickedValue", clickedCategoryButton.value);
                }
                this.clickedCategoryButton = clickedCategoryButton;
            }
        });

        this.postSearchButton.addEventListener("click", evt => {
            this.postSearchKeywordInput.value = this.getRemoveSpaceStr(this.postSearchKeywordInput.value);

            if (this.postSearchKeywordInput.value === '') {
                this.showToastMessage("키워드를 한글자 이상 입력해주세요.");
                return;
            }
            this.postSearchForm.submit();
        });

        if (this.categorySettingButton != null) {
            this.categorySettingButton.addEventListener("click", evt => {
                this.openPopUp(1080, 750, '/category/setting?blogId=' + document.getElementById("blog_info_id").value, 'popup');
            })
        }

        this.audioPlayerCategoryList.addEventListener("change", evt => {
            this.showToastMessage("뮤직 카테고리 변경");
        });

        this.audioPlayerSettingButton.addEventListener("click", evt => {
            this.showToastMessage("뮤직 설정 버튼 클릭");
        });

        this.musicLiveSettingButton.addEventListener("click", evt => {
            this.showToastMessage("뮤직 재생 설정 버튼 클릭");
        });
    }

    #requestRecentPostCard() {
        const xhr = new XMLHttpRequest();
        const blogId = document.getElementById("blog_info_id").value;
        xhr.open("GET", `/post/recent/${blogId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#recentPostHandleTemplateList(responseValue);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("게시글 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #requestPopularPostCard() {
        const xhr = new XMLHttpRequest();
        const blogId = document.getElementById("blog_info_id").value;
        xhr.open("GET", `/post/popular/${blogId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#popularPostHandleTemplateList(responseValue);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("게시글 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #recentPostHandleTemplateList(postDto) {
        const recentPostCardTemplate = document.getElementById("recent-post-card-template").innerHTML;
        const recentPostCardTemplateObject = Handlebars.compile(recentPostCardTemplate);
        const recentPostCardTemplateHTML = recentPostCardTemplateObject(postDto);
        this.recentPostCardList.innerHTML = recentPostCardTemplateHTML;
    }

    #popularPostHandleTemplateList(postDto) {
        const popularPostCardTemplate = document.getElementById("popular-post-card-template").innerHTML;
        const popularPostCardTemplateObject = Handlebars.compile(popularPostCardTemplate);
        const popularPostCardTemplateHTML = popularPostCardTemplateObject(postDto);
        this.popularPostCardList.innerHTML = popularPostCardTemplateHTML;
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const blogHeaderController = new BlogHeaderController();
    blogHeaderController.initBlogHeaderController();
});