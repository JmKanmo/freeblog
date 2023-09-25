class MusicPlayController extends UtilController {
    constructor() {
        super();
        this.musicStoreListSearchInput = document.getElementById("musicStoreListSearchInput");
        this.musicStoreListCategorySelector = document.getElementById("musicStoreListCategorySelector");
        this.musicStoreListSortSelector = document.getElementById("musicStoreListSortSelector");
        this.musicStoreListBox = document.getElementById("musicStoreListBox");
        this.musicStorePagination = document.getElementById("musicStorePagination");
        // page size
        this.musicStoreRecordSize = 5;
        this.musicStorePageSize = 5;
    }

    initMusicPlayController() {
        this.#requestMusicStoreList("/music/play-list");
        this.initEventListener();
    }

    #requestMusicStoreList(url, page) {
        const musicStoreSearchKeywordValue = !this.musicStoreListSearchInput.value ? '' : this.musicStoreListSearchInput.value;
        const musicStoreListCategoryValue = !this.musicStoreListCategorySelector.value ? -1 : this.musicStoreListCategorySelector.value;
        const musicStoreListOrderByValue = !this.musicStoreListSortSelector.value ? "ASC" : '';
        const musicStoreKeywordTypeValue = `ALL`;
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
                    this.#handleTemplateList(responseValue);
                    this.#clearPagination();
                    return;
                }

                this.#handleTemplateList(responseValue);
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
    }

    #handleTemplateList(responseValue) {
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