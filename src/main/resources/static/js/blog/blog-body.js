class BlogBodyController extends UtilController {
    constructor() {
        super();
        this.blogId = document.getElementById("blog_info_id").value;
        this.blogPostCategoryTextBox = document.getElementById("blog_post_category_text_box");
        this.blogPostList = document.getElementById("blog_post_list");
        this.blogPostErrorContainer = document.getElementById("blog_post_error_container");
        this.prevClickedCategoryValue = null;
    }

    initBlogBodyController() {
        this.initBlogPostDocument();
        this.requestAllBlogPost(`/post/all/${this.blogId}`);
        this.initBlogBodyEventListener();
    }

    requestAllBlogPost(url) {
        const xhr = new XMLHttpRequest();

        xhr.open("GET", url);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (status >= 400 && status <= 500) {
                this.blogPostErrorContainer.style.display = 'block';
                const blogPostErrorMessageTemplate = document.getElementById("blog-post-error-message-template").innerHTML;
                const blogPostErrorMessageTemplateObject = Handlebars.compile(blogPostErrorMessageTemplate);
                const blogPostErrorMessageTemplateHTML = blogPostErrorMessageTemplateObject(responseValue);
                this.blogPostErrorContainer.innerHTML = blogPostErrorMessageTemplateHTML;
            } else {
                const blogPostCategoryTemplate = document.getElementById("blog-post-category-template").innerHTML;
                const blogPostCategoryTemplateObject = Handlebars.compile(blogPostCategoryTemplate);
                const blogPostCategoryTemplateHTML = blogPostCategoryTemplateObject(responseValue["postTotalDto"]["postSummaryDto"]);
                this.blogPostCategoryTextBox.innerHTML = blogPostCategoryTemplateHTML;

                const blogPostListTemplate = document.getElementById("blog-post-image-template").innerHTML;
                const blogPostListTemplateObject = Handlebars.compile(blogPostListTemplate);
                const blogPostListTemplateHTML = blogPostListTemplateObject(responseValue["postTotalDto"]);
                this.blogPostList.innerHTML = blogPostListTemplateHTML;
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("블로그 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    initBlogPostDocument() {
        this.blogPostErrorContainer.style.display = 'none';
    }

    initBlogBodyEventListener() {
        this.blogPostList.addEventListener("click", evt => {
            const clickedCategoryButton = evt.target.closest(".common_button_text");
            const prevClickedCategory = document.querySelector('[value = "' + localStorage.getItem("clickedCategoryButton") + '"]');

            if (prevClickedCategory != null) {
                prevClickedCategory.style.fontWeight = "normal";
                prevClickedCategory.style.textDecoration = "none";
            }

            if (clickedCategoryButton != null && this.prevClickedCategoryValue != clickedCategoryButton.innerText) {
                this.requestAllBlogPost(clickedCategoryButton.value);
                this.prevClickedCategoryValue = clickedCategoryButton.innerText;
            }
        });
    }
}

// document.getElementById("blog_info_id").value
document.addEventListener('DOMContentLoaded', function () {
    const blogBodyController = new BlogBodyController();
    blogBodyController.initBlogBodyController();
});