class NoticeListController extends UtilController {
    constructor() {
        super();
        this.noticeListPagination = document.getElementById("noticeListPagination");
        this.noticeList = document.getElementById("notice_list");
        // page size
        this.noticeRecordSize = 10;
        this.noticePageSize = 10;
    }

    initNoticeListController() {
        this.requestNoticeList(`/notice/search-list`);
        this.initEventListener();
    }

    initEventListener() {
        this.noticeListPagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.requestNoticeList(url, page);
                }
            }
        });
    }

    requestNoticeList(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.noticeRecordSize, this.noticePageSize);

        xhr.open("GET", url + '?' + queryParam.toString(), true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue["noticePaginationResponse"]["noticeDto"].length <= 0) {
                    this.#handleTemplateList(responseValue);
                    this.#clearPagination();
                    return;
                }

                this.#handleTemplateList(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["noticePaginationResponse"]["noticePagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("공지사항 목록 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #handleTemplateList(responseValue) {
        const noticeListTemplate = document.getElementById("notice-template").innerHTML;
        const noticeListTemplateObject = Handlebars.compile(noticeListTemplate);
        const jsonObj = responseValue["noticePaginationResponse"]["noticeDto"];
        const noticeListTemplateHTML = noticeListTemplateObject({"noticeDtoList": jsonObj});
        this.noticeList.innerHTML = noticeListTemplateHTML;
    }

    #clearPagination() {
        this.noticeListPagination.innerHTML = ``;
    }

    #handlePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.noticeListPagination.innerHTML = '';
            return;
        }
        this.noticeListPagination.innerHTML = this.drawBasicPagination(pagination, queryParam, url);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const noticeListController = new NoticeListController();
    noticeListController.initNoticeListController();
});