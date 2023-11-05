class BlogViewController extends UtilController {
    constructor() {
        super();
        this.blog_view_select_sort_turn = document.getElementById("blog_view_select_sort_turn");
        this.blogSearchOptionSelector = document.getElementById("blog_search_option_selector");
        this.musicHeaderController = new MusicHeaderController();
    }

    initBlogViewController() {
        this.blog_view_select_sort_turn.addEventListener("change", evt => {
            this.requestHttpBlogView([evt.target.value, this.blog_view_select_sort_term.value]);
        });

        this.blogSearchOptionSelector.addEventListener("change", evt => {
            this.showToastMessage("옵션 변경");
        });

        this.musicHeaderController.initMusicPlayer();
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
});