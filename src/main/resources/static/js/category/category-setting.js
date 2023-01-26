class CategorySettingController extends UtilController {
    constructor() {
        super();
        this.oneLevelCategoryButton = document.getElementById("one_level_category_button");
        this.twoLevelCategoryButton = document.getElementById("two_level_category_button");
        this.deleteCategoryButton = document.getElementById("delete_category_button");
        this.categoryList = document.getElementById("blog_header_category_list");
        this.prevClickedButton = null;
    }

    initCategorySettingController() {
        this.initEventListener();
    }

    initEventListener() {
        this.categoryList.addEventListener("click", evt => {
            const clickedButton = evt.target.closest("button");

            if (clickedButton != null) {
                clickedButton.style.backgroundColor = 'rgb(211 209 209)';

                if (this.prevClickedButton != null && this.prevClickedButton !== clickedButton) {
                    this.prevClickedButton.style.backgroundColor = '#ffffff';
                }
                this.prevClickedButton = clickedButton;
            }
        });

        this.oneLevelCategoryButton.addEventListener("click", evt => {
            const categoryValues = this.getCategoryValues(this.prevClickedButton);

            if (!categoryValues) {
                return;
            }

            console.log(categoryValues);
        });

        this.twoLevelCategoryButton.addEventListener("click", evt => {
            const categoryValues = this.getCategoryValues(this.prevClickedButton);

            if (!categoryValues) {
                return;
            }

            console.log(categoryValues);
        });

        this.deleteCategoryButton.addEventListener("click", evt => {
            const categoryValues = this.getCategoryValues(this.prevClickedButton);

            if (!categoryValues) {
                return;
            }

            console.log(categoryValues);
        });
    }

    getCategoryValues(category) {
        if (!category) {
            this.showToastMessage('대상 카테고리가 선택되지 않았습니다.');
            return null;
        }

        let categoryValue = category.value;

        if (!categoryValue) {
            this.showToastMessage('대상 카테고리 유효성 검사에 실패하였습니다.');
            return null;
        }

        if (category.value === 'totalCategory') {
            return categoryValue;
        }

        categoryValue = category.value.split(":");

        const categoryType = categoryValue[0];
        const categoryValues = [];

        categoryValues.push(categoryType);

        categoryValue = categoryValue[1].split("&");

        categoryValues.push(categoryValue[0]);
        categoryValues.push(categoryValue[1]);
        return categoryValues;
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const categorySettingController = new CategorySettingController();
    categorySettingController.initCategorySettingController();
});