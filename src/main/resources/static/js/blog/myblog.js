class MyBlogController extends UtilController {
    constructor() {
        super();
        this.blocUserNickName = document.getElementById("blog_user_nickname");
        this.initLocalStorage();
    }

    initLocalStorage() {
        localStorage.removeItem("prevClickedValue");
    }

    initMyBlogController() {
        document.title = `${this.blocUserNickName.value}님의 블로그`;
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const myBlogController = new MyBlogController();
    myBlogController.initMyBlogController();
});