class BlogBodyController extends UtilController {
    constructor() {
        super();
        this.blogId = document.getElementById("blog_info_id").value;
        this.blogPostCategoryTextBox = document.getElementById("blog_post_category_text_box");
        this.blogPostList = document.getElementById("blog_post_list");
        this.postPagination = document.getElementById("postPagination");

        this.postRecordSize = 10;
        this.postPageSize = 10;
    }

    initBlogBodyController() {
        this.requestAllBlogPost(`/post/all/${this.blogId}`);
        this.initBlogBodyEventListener();
    }

    requestAllBlogPost(url, page) {
        const xhr = new XMLHttpRequest();
        const spinner = this.loadingSpin({
            lines: 15, length: 5, width: 5,
            radius: 10, scale: 1, corners: 1, color: '#000', opacity: 0.25,
            rotate: 0, direction: 1, speed: 1, trail: 60, fps: 20, zIndex: 2e9,
            className: 'spinner', top: '25%', left: '10%',
            shadow: false, hwaccel: false, position: 'absolute'
        }, "blogMainPostLoading");
        const queryParam = this.getQueryParam(page, this.postRecordSize, this.postPageSize);

        xhr.open("GET", url + '?' + queryParam.toString(), true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showSweetAlertErrorMessage(responseValue["message"]);
                this.loadingStop(spinner, "blogMainPostLoading");
            } else {
                this.loadingStop(spinner, "blogMainPostLoading");

                if (responseValue["postPaginationResponse"]["postDto"]["postSummaryDto"]["count"] <= 0) {
                    this.#handleTemplateList(responseValue, true);
                    this.#clearPagination();
                    return;
                }

                this.#handleTemplateList(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["postPaginationResponse"]["postPagination"], queryParam, url);
                // 제일 위로 스크롤 이동
                this.scrollTargetElement("top");
            }
        });

        xhr.addEventListener("error", event => {
            this.showSweetAlertErrorMessage("블로그 정보를 불러오는데 실패하였습니다.");
            this.loadingStop(spinner, "blogMainPostLoading");
        });
        xhr.send();
    }

    #handleTemplateList(responseValue, empty = false) {
        const blogPostCategoryTemplate = document.getElementById("blog-post-category-template").innerHTML;
        const blogPostCategoryTemplateObject = Handlebars.compile(blogPostCategoryTemplate);
        const blogPostCategoryTemplateHTML = blogPostCategoryTemplateObject(responseValue["postPaginationResponse"]["postDto"]["postSummaryDto"]);
        this.blogPostCategoryTextBox.innerHTML = blogPostCategoryTemplateHTML;

        if (empty) {
            this.blogPostList.innerHTML = `<h3 class="common_gray_text">게시글이 존재하지 않습니다.</h3>`;
        } else {
            const blogPostListTemplate = document.getElementById("blog-post-image-template").innerHTML;
            const blogPostListTemplateObject = Handlebars.compile(blogPostListTemplate);
            const blogPostListTemplateHTML = blogPostListTemplateObject(responseValue["postPaginationResponse"]["postDto"]);
            this.blogPostList.innerHTML = blogPostListTemplateHTML;
        }
    }

    #handlePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.postPagination.innerHTML = '';
            return;
        }
        this.postPagination.innerHTML = this.drawBasicPagination(pagination, queryParam, url);
    }

    #clearPagination() {
        this.postPagination.innerHTML = ``;
    }

    initBlogBodyEventListener() {
        this.blogPostList.addEventListener("click", evt => {
            const clickedCategoryButton = evt.target.closest(".category_search_target");
            const prevClickedCategory = document.querySelector('[value = "' + localStorage.getItem("prevClickedValue") + '"]');

            if (prevClickedCategory != null) {
                prevClickedCategory.style.fontWeight = "normal";
                prevClickedCategory.style.textDecoration = "none";
            }

            if (clickedCategoryButton != null && localStorage.getItem("prevClickedValue") !== clickedCategoryButton.value) {
                this.requestAllBlogPost(clickedCategoryButton.value);
                localStorage.setItem("prevClickedValue", clickedCategoryButton.value);
            }
        });

        this.postPagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.requestAllBlogPost(url, page);
                }
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const blogBodyController = new BlogBodyController();
    blogBodyController.initBlogBodyController();
});