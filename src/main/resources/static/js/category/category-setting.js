class CategorySettingController extends UtilController {
    constructor() {
        super();
        this.oneLevelCategoryButton = document.getElementById("one_level_category_button");
        this.twoLevelCategoryButton = document.getElementById("two_level_category_button");
        this.deleteCategoryButton = document.getElementById("delete_category_button");
    }

    initCategorySettingController() {
        this.initEventListener();
    }

    initEventListener() {
        this.oneLevelCategoryButton.addEventListener("click", evt => {
            this.showToastMessage("1단 카테고리 추가");
        });

        this.twoLevelCategoryButton.addEventListener("click", evt => {
            this.showToastMessage("2단 카테고리 추가");
        });

        this.deleteCategoryButton.addEventListener("click", evt => {
            this.showToastMessage("카테고리 삭제");
        });
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const categorySettingController = new CategorySettingController();
    categorySettingController.initCategorySettingController();
});