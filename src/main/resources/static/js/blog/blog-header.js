class BlogHeaderController extends BlogBodyController {
    constructor() {
        super();
        this.recentPostTitle = document.getElementById("recent_post_title");
        this.popularPostTitle = document.getElementById("popular_post_title");
        this.recentPostBlock = document.getElementById("recent_post_container");
        this.popularPostBlock = document.getElementById("popular_post_container");
        this.audioPlayer = this.initAudioPlayer(); // TODO
        this.introButton = document.getElementById("intro_button");

        this.blogHeaderCategoryList = document.getElementById("blog_header_category_list");
        this.clickedCategoryButton = null;
        this.postSearchButton = document.getElementById("postSearchButton");
        this.postSearchForm = document.getElementById("postSearchForm");
        this.categorySettingButton = document.getElementById("category_setting_button");

        this.recentPostCardList = document.getElementById("recent_post_card_list");
        this.polularPostCardList = document.getElementById("popular_post_card_list");
    }

    initBlogHeaderController() {
        this.initDefault();
        this.initCalendar();
        this.initBlogHeaderEventListener();
    }

    initDefault() {
        this.recentPostTitle.style.color = '#333';
        this.recentPostBlock.style.display = 'block';
    }

    initBlogHeaderEventListener() {
        this.recentPostTitle.addEventListener("click", evt => {
            this.recentPostTitle.style.color = '#333';
            this.recentPostBlock.style.display = 'block';
            this.popularPostTitle.style.color = '#777';
            this.popularPostBlock.style.display = 'none';
        });

        this.popularPostTitle.addEventListener("click", evt => {
            this.popularPostTitle.style.color = '#333';
            this.popularPostBlock.style.display = 'block';
            this.recentPostTitle.style.color = '#777';
            this.recentPostBlock.style.display = 'none';
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
        xhr.open("GET", `/post/recent/${document.getElementById("blog_info_id").value}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleTemplateList(responseValue["postPaginationResponse"]["postDto"]);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("게시글 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #requestPopularPostCard() {
        this.showToastMessage("인기글 기능은 추후에 개발 될 예정입니다.");
    }

    #handleTemplateList(postDto) {

    }
}

document.addEventListener('DOMContentLoaded', function () {
    const blogHeaderController = new BlogHeaderController();
    blogHeaderController.initBlogHeaderController();
});