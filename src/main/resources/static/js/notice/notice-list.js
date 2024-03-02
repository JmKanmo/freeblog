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
        const spinner = this.loadingSpin({
            lines: 15,
            length: 4,
            width: 4,
            radius: 4,
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
            top: '50%',
            left: '50%',
            shadow: false,
            hwaccel: false,
            position: 'absolute'
        }, "noticeLoading");
        const queryParam = this.getQueryParam(page, this.noticeRecordSize, this.noticePageSize);

        xhr.open("GET", url + '?' + queryParam.toString(), true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showSweetAlertErrorMessage(responseValue["message"]);
                this.loadingStop(spinner, "noticeLoading");
            } else {
                this.loadingStop(spinner, "noticeLoading");

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
            this.showSweetAlertErrorMessage("공지사항 목록 정보를 불러오는데 실패하였습니다.");
            this.loadingStop(spinner, "noticeLoading");
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