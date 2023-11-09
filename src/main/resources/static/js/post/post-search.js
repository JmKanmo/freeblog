class PostSearchController extends UtilController {
    constructor() {
        super();
        this.blogIdInput = document.getElementById("blogIdInput");
        this.keywordInput = document.getElementById("keywordInput");
        this.searchOptionInput = document.getElementById("searchOptionInput");
        this.postSearchCount = document.getElementById("post_search_count");
        this.postSearchList = document.getElementById("blog_post_list");
        this.postSearchPagination = document.getElementById("PostSearchPagination");
        this.searchPauseBox = document.getElementById("search_pause_box");
        this.searchResultBox = document.getElementById("search_result_box");
        this.postSearchKeywordInput = document.getElementById("postSearchKeywordInput");
        this.postSearchButton = document.getElementById("postSearchButton");
        this.postSearchOptionSelector = document.getElementById("postSearchOptionSelector");
        this.postRecordSize = 5;
        this.postPageSize = 5;

        this.musicHeaderController = new MusicHeaderController();
    }

    initPostSearchController() {
        this.initPostTitle();
        this.initEventListener();
        this.#requestPostSearch("/post/search-rest");
        this.musicHeaderController.initMusicPlayer();
    }

    initPostTitle() {
        document.title = `'${this.keywordInput.value}' 검색 결과`;
    }

    initEventListener() {
        this.postSearchButton.addEventListener("click", evt => {
            this.#requestPostSearchSelf("/post/search-rest");
        });

        this.postSearchKeywordInput.addEventListener("keyup", evt => {
            if (evt.keyCode == 13) {
                this.#requestPostSearchSelf("/post/search-rest");
            }
        });

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

    #requestPostSearchSelf(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.postRecordSize, this.postPageSize);
        const totalUrl = url + '?' + "blogId=" + this.blogIdInput.value + "&" + "keyword=" + this.postSearchKeywordInput.value + "&" + "searchOption=" + this.postSearchOptionSelector.value
            + "&" + queryParam.toString();

        xhr.open("GET", totalUrl, true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            this.searchPauseBox.style.display = 'none';
            this.searchResultBox.style.display = 'block';

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                const totalCount = responseValue["postPaginationResponse"]["postPagination"]["totalRecordCount"];

                this.postSearchCount.innerText = totalCount;
                document.title = `'${this.postSearchKeywordInput.value}' 검색 결과`;
                this.searchResultBox.innerHTML = `
                <span>"${this.postSearchKeywordInput.value}"</span>
                <span>검색 결과: <span style="color: #333" id="post_search_count">${totalCount}</span>개</span>
                `;

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
            this.showToastMessage("검색 게시글 정보를 불러오는데 실패하였습니다.");
        });

        xhr.send();
    }

    #requestPostSearch(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.postRecordSize, this.postPageSize);
        const totalUrl = url + '?' + "blogId=" + this.blogIdInput.value + "&" + "keyword=" + this.keywordInput.value + "&" + "searchOption="
            + this.searchOptionInput.value + "&" + queryParam.toString();

        xhr.open("GET", totalUrl, true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            this.searchPauseBox.style.display = 'none';
            this.searchResultBox.style.display = 'block';

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
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
            this.showToastMessage("검색 게시글 정보를 불러오는데 실패하였습니다.");
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
    const postSearchController = new PostSearchController();
    postSearchController.initPostSearchController();
});