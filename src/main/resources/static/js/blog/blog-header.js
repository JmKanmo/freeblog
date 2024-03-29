class BlogHeaderController extends BlogBodyController {
    constructor() {
        super();
        this.recentPostTitle = document.getElementById("recent_post_title");
        this.popularPostTitle = document.getElementById("popular_post_title");
        this.postSearchKeywordInput = document.getElementById("postSearchKeywordInput");
        this.postSearchKeywordHiddenInput = document.getElementById("postSearchKeywordHiddenInput");
        this.postSearchOptionSelector = document.getElementById("postSearchOptionSelector");
        this.postSearchOptionHiddenInput = document.getElementById("postSearchOptionHiddenInput");

        this.introButton = document.getElementById("intro_button");

        this.blogHeaderCategoryList = document.getElementById("blog_header_category_list");
        this.clickedCategoryButton = null;
        this.postSearchButton = document.getElementById("postSearchButton");
        this.postSearchForm = document.getElementById("postSearchForm");
        this.categorySettingButton = document.getElementById("category_setting_button");

        this.recentPostCardList = document.getElementById("recent_post_card_list");
        this.popularPostCardList = document.getElementById("popular_post_card_list");
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
            this.openPopUp(1080, 750, '/user/intro', 'popup');
        });

        // category button event listener
        this.blogHeaderCategoryList.addEventListener("click", evt => {
            const clickedCategoryButton = evt.target.closest(".category_search_target");

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

        this.postSearchKeywordInput.addEventListener("keyup", evt => {
            if (evt.keyCode == 13) {
                this.postSearchKeywordInput.value = this.getRemoveSpaceStr(this.postSearchKeywordInput.value);
                this.postSearchKeywordHiddenInput.value = this.postSearchKeywordInput.value;
                this.postSearchOptionHiddenInput.value = this.postSearchOptionSelector.value;

                if (this.postSearchKeywordInput.value === '') {
                    this.showSweetAlertWarningMessage("키워드를 한글자 이상 입력해주세요.");
                    return;
                }
                this.postSearchForm.submit();
            }
        });

        this.postSearchButton.addEventListener("click", evt => {
            this.postSearchKeywordInput.value = this.getRemoveSpaceStr(this.postSearchKeywordInput.value);
            this.postSearchKeywordHiddenInput.value = this.postSearchKeywordInput.value;
            this.postSearchOptionHiddenInput.value = this.postSearchOptionSelector.value;

            if (this.postSearchKeywordInput.value === '') {
                this.showSweetAlertWarningMessage("키워드를 한글자 이상 입력해주세요.");
                return;
            }
            this.postSearchForm.submit();
        });

        if (this.categorySettingButton != null) {
            this.categorySettingButton.addEventListener("click", evt => {
                this.openPopUp(1080, 750, '/category/setting?blogId=' + document.getElementById("blog_info_id").value, 'popup');
            })
        }
    }

    #requestRecentPostCard() {
        const xhr = new XMLHttpRequest();
        const blogId = document.getElementById("blog_info_id").value;
        xhr.open("GET", `/post/recent/${blogId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showSweetAlertErrorMessage(responseValue["message"]);
            } else {
                this.#recentPostHandleTemplateList(responseValue);
            }
        });

        xhr.addEventListener("error", event => {
            this.showSweetAlertErrorMessage("게시글 정보를 불러오는데 실패하였습니다.");
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
                this.showSweetAlertErrorMessage(responseValue["message"]);
            } else {
                this.#popularPostHandleTemplateList(responseValue);
            }
        });

        xhr.addEventListener("error", event => {
            this.showSweetAlertErrorMessage("게시글 정보를 불러오는데 실패하였습니다.");
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