class BlogViewController extends UtilController {
    constructor() {
        super();
        this.searchBlogInput = document.getElementById("search_blog_input");
        this.blogSearchOptionSelector = document.getElementById("blog_search_option_selector");
        this.searchBlogButton = document.getElementById("searchBlogButton");
        this.blogViewSelectSortTurn = document.getElementById("blog_view_select_sort_turn");
        this.blogViewSearchForm = document.getElementById("blogViewSearchForm");
        this.musicHeaderController = new MusicHeaderController();
    }

    initBlogViewController() {
        this.initEventListener();
        this.musicHeaderController.initMusicPlayer();
    }

    initEventListener() {
        this.searchBlogInput.addEventListener("keyup", evt => {
            if (evt.keyCode == 13) {
                this.showToastMessage("todo, key up");
            }
        })

        this.searchBlogButton.addEventListener("click", evt => {
            this.showToastMessage("todo, click");
        });
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const blogViewController = new BlogViewController();
    blogViewController.initBlogViewController();
});