class BlogViewController extends UtilController {
    constructor() {
        super();
        this.blog_view_select_sort_turn = document.getElementById("blog_view_select_sort_turn");
        this.blog_view_select_sort_term = document.getElementById("blog_view_select_sort_term");
    }

    initBlogViewController() {
        this.blog_view_select_sort_turn.addEventListener("change", evt => {
            this.requestHttpBlogView([evt.target.value, this.blog_view_select_sort_term.value]);
        });

        this.blog_view_select_sort_term.addEventListener("change", evt => {
            this.requestHttpBlogView([evt.target.value, this.blog_view_select_sort_term.value]);
        });
    }

    requestHttpBlogView(parameter) {
        this.fetchBlogView("/post" + `?turn=${parameter[0]}&term=${parameter[1]}`).then(blogViewData => {
            if (blogViewData !== undefined) {
                this.showToastMessage("result: " + blogViewData);
                // TODO
                // const blogViewListTemplate = document.querySelector("#banner-image-list-template").innerHTML;
                // const template = Handlebars.compile(blogViewListTemplate);
                // const templateHTML = template({blogViewList: blogViewData});
                // document.getElementById("banner-carousel-board").innerHTML += templateHTML;
            }
        });
    }

    async fetchBlogView(url) {
        try {
            const response = await fetch(url, {method: 'GET'});
            const responseJson = await response.json();
            return responseJson;
        } catch (error) {
            this.showToastMessage("[BlogViewController:fetchBlogView] error =>" + error);
            return undefined;
        }
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const blogViewController = new BlogViewController();
    blogViewController.initBlogViewController();

    const loginPolUpController = new LoginPopUpController();
    loginPolUpController.initLoginPopUpController();
});