class TagController extends UtilController {
    constructor() {
        super();
        this.blogIdInput = document.getElementById("blog_id_input");
        this.tagNameInput = document.getElementById("tag_name_input");
        this.postSearchCount = document.getElementById("post_search_count");
        this.postSearchList = document.getElementById("blog_post_list");
        this.postSearchPagination = document.getElementById("PostSearchPagination");

        this.postRecordSize = 5;
        this.postPageSize = 5;
    }

    initTagController() {
        this.initTagTitle();
        this.initEventListener();
        this.#requestPostSearch();
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
        const queryParam = this.getQueryParam(page, this.postRecordSize, this.postPageSize);
        // TODO
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const tagController = new TagController();
    tagController.initTagController();
});