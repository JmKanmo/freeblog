class TagController extends UtilController {
    constructor() {
        super();
        this.blogIdInput = document.getElementById("blog_id_input");
        this.tagNameInput = document.getElementById("tag_name_input");
        this.postSearchCount = document.getElementById("post_search_count");
        this.postSearchList = document.getElementById("blog_post_list");
        this.postSearchPagination = document.getElementById("PostSearchPagination");
        this.searchPauseBox = document.getElementById("search_pause_box");
        this.searchResultBox = document.getElementById("search_result_box");

        this.postRecordSize = 15;
        this.postPageSize = 15;

        this.musicHeaderController = new MusicHeaderController();
    }

    initTagController() {
        this.initTagTitle();
        this.initEventListener();
        this.#requestPostSearch("/tag/search-post");
        this.musicHeaderController.initMusicPlayer();
    }

    initTagTitle() {
        document.title = `'${this.tagNameInput.value}' 검색 결과`;
    }

    initEventListener() {
        this.postSearchPagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestPostSearch(url, page);
                }
            }
        });
    }

    #requestPostSearch(url, page) {
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
        }, "postSearchLoading");
        const queryParam = this.getQueryParam(page, this.postRecordSize, this.postPageSize);
        const totalUrl = url + '?' + "blogId=" + this.blogIdInput.value + '&' + "tagKeyword=" + this.tagNameInput.value + '&' + queryParam.toString();

        xhr.open("GET", totalUrl, true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            this.searchPauseBox.style.display = 'none';
            this.searchResultBox.style.display = 'block';

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showSweetAlertErrorMessage(responseValue["message"]);
                this.loadingStop(spinner, "postSearchLoading");
            } else {
                this.loadingStop(spinner, "postSearchLoading");
                const totalCount = responseValue["postPaginationResponse"]["postPagination"]["totalRecordCount"];
                this.postSearchCount.innerText = totalCount;

                if (totalCount <= 0) {
                    this.#handleTemplateList(responseValue);
                    this.#clearPagination();
                    return;
                }

                this.#handleTemplateList(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["postPaginationResponse"]["postPagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showSweetAlertErrorMessage("검색 게시글 정보를 불러오는데 실패하였습니다.");
            this.loadingStop(spinner, "postSearchLoading");
        });
        xhr.send();
    }

    #handleTemplateList(responseValue) {
        const postSearchTemplate = document.getElementById("post_search_template").innerHTML;
        const postSearchTemplateObject = Handlebars.compile(postSearchTemplate);
        const postSearchTemplateHTML = postSearchTemplateObject(responseValue["postPaginationResponse"]["postDto"]);
        this.postSearchList.innerHTML = postSearchTemplateHTML;
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
    const tagController = new TagController();
    tagController.initTagController();
});