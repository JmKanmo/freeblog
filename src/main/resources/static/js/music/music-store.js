class MusicPlayController extends UtilController {
    constructor() {
        super();
        this.musicStoreListSearchInput = document.getElementById("musicStoreListSearchInput");
        this.musicStoreListCategorySelector = document.getElementById("musicStoreListCategorySelector");
        this.musicStoreListSortSelector = document.getElementById("musicStoreListSortSelector");
        this.musicStoreListBox = document.getElementById("musicStoreListBox");
        this.musicStorePagination = document.getElementById("musicStorePagination");
        this.musicStoreListSearchButton = document.getElementById("musicStoreListSearchButton");
        this.musicSearchTypeSelector = document.getElementById("musicSearchTypeSelector");
        this.musicStoreReloadButton = document.getElementById("musicStoreReloadButton");

        // page size
        this.musicStoreRecordSize = 5;
        this.musicStorePageSize = 5;
    }

    initMusicPlayController() {
        this.#requestMusicCategoryList();
        this.#requestMusicStoreList("/music/play-list");
        this.initEventListener();
    }

    #requestMusicCategoryList() {
        const xhr = new XMLHttpRequest();

        xhr.open("GET", `/music-category/list`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleMusicCategoryTemplate(responseValue);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 카테고리 목록 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #requestMusicStoreList(url, page) {
        const musicStoreSearchKeywordValue = !this.musicStoreListSearchInput.value ? '' : this.musicStoreListSearchInput.value;
        const musicStoreListCategoryValue = !this.musicStoreListCategorySelector.value ? -1 : this.musicStoreListCategorySelector.value;
        const musicStoreListOrderByValue = !this.musicStoreListSortSelector.value ? "ASC" : this.musicStoreListSortSelector.value;
        const musicStoreKeywordTypeValue = this.musicSearchTypeSelector.value;
        const musicStoreSearchTypeValue = `LIKE`;  // TODO 실제 서비스 시에는 FULL-TEXT 방식 고려

        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.musicStoreRecordSize, this.musicStorePageSize);

        xhr.open("GET"
            , url + '?' + queryParam.toString()
            + `&categoryId=${musicStoreListCategoryValue}&keyword=${musicStoreSearchKeywordValue}&orderBy=${musicStoreListOrderByValue}&keywordType=${musicStoreKeywordTypeValue}&searchType=${musicStoreSearchTypeValue}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue["musicPaginationResponse"]["musicDto"].length <= 0) {
                    this.showToastMessage("해당 카테고리 내 뮤직 플레이 리스트가 비었습니다.", 100);
                    this.#handleMusicTemplate(responseValue);
                    this.#clearPagination();
                    return;
                }

                this.#handleMusicTemplate(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["musicPaginationResponse"]["musicPagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 목록 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    initEventListener() {
        this.musicStorePagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestMusicStoreList(url, page);
                }
            }
        });

        this.musicStoreListCategorySelector.addEventListener("change", evt => {
            this.#requestMusicStoreList("/music/play-list");
        });

        this.musicStoreListSearchInput.addEventListener("keyup", evt => {
            if (evt.keyCode == 13) {
                this.#requestMusicStoreList("/music/play-list");
            }
        });

        this.musicStoreListSearchButton.addEventListener("click", evt => {
            this.#requestMusicStoreList("/music/play-list");
        });

        this.musicStoreListSortSelector.addEventListener("change", evt => {
            this.#requestMusicStoreList("/music/play-list");
        });

        this.musicStoreReloadButton.addEventListener("click", evt => {
            
            this.#requestMusicCategoryList();
            this.#requestMusicStoreList("/music/play-list");
        });
    }

    #handleMusicCategoryTemplate(responseValue) {
        const musicCategoryTemplate = document.getElementById("music-category-template").innerHTML;
        const musicCategoryTemplateObject = Handlebars.compile(musicCategoryTemplate);
        const jsonObj = responseValue["musicPaginationResponse"];
        const musicTemplateHTML = musicCategoryTemplateObject({"musicCategoryList": jsonObj});
        this.musicStoreListCategorySelector.innerHTML = musicTemplateHTML;
    }

    #handleMusicTemplate(responseValue) {
        const musicTemplate = document.getElementById("music-template").innerHTML;
        const musicTemplateObject = Handlebars.compile(musicTemplate);
        const jsonObj = responseValue["musicPaginationResponse"]["musicDto"];
        const musicTemplateHTML = musicTemplateObject({"musicList": jsonObj});
        this.musicStoreListBox.innerHTML = musicTemplateHTML;
    }

    #clearPagination() {
        this.musicStorePagination.innerHTML = ``;
    }

    #handlePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.musicStorePagination.innerHTML = '';
            return;
        }
        this.musicStorePagination.innerHTML = this.drawBasicPagination(pagination, queryParam, url);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const musicPlayController = new MusicPlayController();
    musicPlayController.initMusicPlayController();
});