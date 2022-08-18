class BlogHeaderController extends UtilController {
    constructor() {
        super();
        this.recentPostTitle = document.getElementById("recent_post_title");
        this.popularPostTitle = document.getElementById("popular_post_title");
        this.recentPostBlock = document.getElementById("recent_post_container");
        this.popularPostBlock = document.getElementById("popular_post_container");
    }

    initBlogHeaderController() {
        this.initDefault();
        this.initCalendar();
        this.initEventListener();
    }

    initDefault() {
        this.recentPostTitle.style.color = '#333';
        this.recentPostBlock.style.display = 'block';
    }

    initEventListener() {
        this.recentPostTitle.addEventListener("click", evt => {
            this.recentPostTitle.style.color = '#333';
            this.recentPostBlock.style.display = 'block';
            this.popularPostTitle.style.color = '#777';
            this.popularPostBlock.style.display = 'none';
        });

        this.popularPostTitle.addEventListener("click", evt => {
            this.popularPostTitle.style.color = '#333';
            this.popularPostBlock.style.display = 'block';
            this.recentPostTitle.style.color = '#777';
            this.recentPostBlock.style.display = 'none';
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const blogHeaderController = new BlogHeaderController();
    blogHeaderController.initBlogHeaderController();
});