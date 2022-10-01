class BlogBodyController extends UtilController {
    constructor() {
        super();
        this.blogId = document.getElementById("blog_info_id").value;
        this.blogPostCategoryTextBox = document.getElementById("blog_post_category_text_box");
        this.blogPostList = document.getElementById("blog_post_list");
        this.pagination = document.getElementById("pagination");
    }

    initBlogBodyController() {
        this.requestAllBlogPost(`/post/all/${this.blogId}`);
        this.initBlogBodyEventListener();
    }

    requestAllBlogPost(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page);

        xhr.open("GET", url + '?' + queryParam.toString());

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(JSON.stringify(responseValue));
            } else {
                if (responseValue["paginationResponse"]["postTotalDto"]["postSummaryDto"]["count"] <= 0) {
                    this.#handleTemplateList(responseValue, true);
                    this.#clearPagination();
                    return;
                }

                this.#handleTemplateList(responseValue);
                this.#clearPagination();
                this.#handlePagination(responseValue["paginationResponse"]["pagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("블로그 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #handleTemplateList(responseValue, empty = false) {
        const blogPostCategoryTemplate = document.getElementById("blog-post-category-template").innerHTML;
        const blogPostCategoryTemplateObject = Handlebars.compile(blogPostCategoryTemplate);
        const blogPostCategoryTemplateHTML = blogPostCategoryTemplateObject(responseValue["paginationResponse"]["postTotalDto"]["postSummaryDto"]);
        this.blogPostCategoryTextBox.innerHTML = blogPostCategoryTemplateHTML;

        if (empty) {
            this.blogPostList.innerHTML = `<h3 class="common_gray_text">게시글이 존재하지 않습니다.</h3>`;
        } else {
            const blogPostListTemplate = document.getElementById("blog-post-image-template").innerHTML;
            const blogPostListTemplateObject = Handlebars.compile(blogPostListTemplate);
            const blogPostListTemplateHTML = blogPostListTemplateObject(responseValue["paginationResponse"]["postTotalDto"]);
            this.blogPostList.innerHTML = blogPostListTemplateHTML;
        }
    }

    #handlePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.pagination.innerHTML = '';
            return;
        }
        this.pagination.innerHTML = this.drawPagination(pagination, queryParam, url);
    }

    #clearPagination() {
        this.pagination.innerHTML = ``;
    }

    initBlogBodyEventListener() {
        this.blogPostList.addEventListener("click", evt => {
            const clickedCategoryButton = evt.target.closest(".common_button_text");
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

        this.pagination.addEventListener("click", evt => {
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