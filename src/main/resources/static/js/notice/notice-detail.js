class NoticeDetailController extends UtilController {
    constructor() {
        super();
        this.noticeDetailTitle = document.getElementById("notice_detail_title");
        this.noticeDetailContents = this.getReadOnlyQuillEditor("notice_detail_contents");
        this.noticeListButton = document.getElementById("notice_list_button");
        this.noticeDetailDeleteButton = document.getElementById("notice_detail_delete_button");
        this.noticeDeleteForm = document.getElementById("notice_delete_form");
    }

    initNoticeDetailController() {
        this.initNoticeTitle();
        this.initNoticeContent();
        this.initEventListener();
    }

    initNoticeTitle() {
        document.title = `공지사항: ${this.noticeDetailTitle.innerText}`;
    }

    initNoticeContent() {
        const decompressedContent = this.compressContent(document.getElementById("hidden_notice_content").value, false);
        document.getElementById("notice_detail_contents").innerHTML = this.getQuillHTML(decompressedContent, false, false);
    }

    initEventListener() {
        this.noticeListButton.addEventListener("click", evt => {
            location.href = `/notice/list`;
        });

        this.noticeDetailDeleteButton.addEventListener("click", evt => {
            if (confirm("공지사항 게시글을 삭제하겠습니까?")) {
                this.noticeDeleteForm.submit();
            }
        });
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const noticeDetailController = new NoticeDetailController();
    noticeDetailController.initNoticeDetailController();
});