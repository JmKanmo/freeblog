class BlogViewController extends UtilController {
    constructor() {
        super();
        this.searchBlogInput = document.getElementById("search_blog_input");
        this.blogSearchOptionSelector = document.getElementById("blog_search_option_selector");
        this.searchBlogButton = document.getElementById("searchBlogButton");
        this.blogViewSelectSortOption = document.getElementById("blog_view_select_sort_option");
        this.postSearchPagination = document.getElementById("PostSearchPagination");
        this.blogPostList = document.getElementById("blog_post_list");

        this.prevPostSearchReloadTime = 0;
        this.postSearchReloadTimeOut = 100;

        this.postRecordSize = 15;
        this.postPageSize = 15;

        this.musicHeaderController = new MusicHeaderController();
    }

    initBlogViewController() {
        this.#requestSearchPost("/search-post");
        this.initEventListener();
        this.musicHeaderController.initMusicPlayer();
    }

    initEventListener() {
        this.searchBlogInput.addEventListener("keyup", evt => {
            if (evt.keyCode == 13) {
                this.#requestSearchPost("/search-post");
            }
        })

        this.searchBlogButton.addEventListener("click", evt => {
            this.#requestSearchPost("/search-post");
        });

        this.postSearchPagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestSearchPost(url, page);
                }
            }
        });

        this.blogViewSelectSortOption.addEventListener("change", evt => {
            this.#requestSearchPost("/search-post");
        })
    }

    #requestSearchPost(url, page) {
        const current = new Date().getTime();

        if (current - this.prevPostSearchReloadTime <= this.postSearchReloadTimeOut) {
            this.showToastMessage("잠시 후에 요청 해주세요.");
            return;
        }
        const searchOption = !this.blogSearchOptionSelector.value ? "title" : this.blogSearchOptionSelector.value;
        const sortOption = !this.blogViewSelectSortOption.value ? "recent" : this.blogViewSelectSortOption.value;
        const keyword = !this.searchBlogInput.value ? "" : this.searchBlogInput.value;
        const queryParam = this.getQueryParam(page, this.postRecordSize, this.postPageSize);
        const totalUrl = `${url}?searchOption=${searchOption}&sortOption=${sortOption}&keyword=${keyword}&${queryParam.toString()}`;
        const xhr = new XMLHttpRequest();
        const spinner = this.loadingSpin({
            lines: 15,
            length: 5,
            width: 5,
            radius: 10,
            scale: 1,
            corners: 1,
            color: '#000',
            opacity: 0.25,
            rotate: 0,
            direction: 1,
            speed: 1,
            trail: 60,
            fps: 20,
            zIndex: 2e9,
            className: 'spinner',
            top: '25%',
            left: '50%',
            shadow: false,
            hwaccel: false,
            position: 'fixed'
        }, "mainPostLoading");

        xhr.open("GET", totalUrl, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
                this.loadingStop(spinner, "mainPostLoading");
            } else {
                this.loadingStop(spinner, "mainPostLoading");
                const totalCount = responseValue["postPaginationResponse"]["postPagination"]["totalRecordCount"];

                if (totalCount > 0) {
                    this.#handleTemplateList(responseValue);
                    this.#clearPagination();
                    this.#handlePagination(responseValue["postPaginationResponse"]["postPagination"], queryParam, url);
                } else {
                    this.blogPostList.innerHTML = `<span class="no_data_span_text">검색 결과가 없습니다.</span>`;
                    this.#clearPagination();
                }
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("검색 게시글 정보를 불러오는데 실패하였습니다.");
            this.loadingStop(spinner, "mainPostLoading");
        });
        xhr.send();
        this.prevPostSearchReloadTime = current;
    }

    #handleTemplateList(responseValue) {
        const postSearchTemplate = document.getElementById("post_search_template").innerHTML;
        const postSearchTemplateObject = Handlebars.compile(postSearchTemplate);
        const postSearchTemplateHTML = postSearchTemplateObject(responseValue["postPaginationResponse"]["postDto"]);
        this.blogPostList.innerHTML = postSearchTemplateHTML;
    }

    #clearPagination() {
        this.postSearchPagination.innerHTML = ``;
    }

    #handlePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.postSearchPagination.innerHTML = '';
            return;
        }
        this.postSearchPagination.innerHTML = this.drawSimplePagination(pagination, queryParam, url);
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const blogViewController = new BlogViewController();
    blogViewController.initBlogViewController();
});