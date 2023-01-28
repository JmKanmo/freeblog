class CategorySettingController extends UtilController {
    constructor() {
        super();
        this.oneLevelCategoryButton = document.getElementById("one_level_category_button");
        this.twoLevelCategoryButton = document.getElementById("two_level_category_button");
        this.updateCategoryButton = document.getElementById("update_category_button");
        this.deleteCategoryButton = document.getElementById("delete_category_button");
        this.categoryList = document.getElementById("blog_header_category_list");
        this.prevClickedButton = null;
        this.registerCategoryButton = document.getElementById("register_category_button");
        this.categoryId = document.getElementById("categoryId").value;
        this.categorySeq = document.getElementById("categorySeq").value;
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
            } else if (categoryValues.length === 4 && categoryValues[0] === 'childCategory' && categoryValues[2] > 0) {
                this.showToastMessage('2차카테고리 에서 해당 작업을 수행할 수 없습니다.');
                return;
            }

            const categoryName = prompt('', '');

            if (categoryName.length <= 0 || /\s/g.test(categoryName)) {
                this.showToastMessage("비어있거나 공백이 포함 된 문자는 등록할 수 없습니다.");
                return;
            }

            this.#addCategory({
                type: 'parentCategory',
                id: ++this.categoryId,
                parentId: categoryValues === 'totalCategory' ? 0 : categoryValues[2],
                seq: ++this.categorySeq,
                name: categoryName
            }, 'one-level');
        });

        this.twoLevelCategoryButton.addEventListener("click", evt => {
            const categoryValues = this.getCategoryValues(this.prevClickedButton);

            if (!categoryValues) {
                return;
            } else if (categoryValues === 'totalCategory') {
                this.showToastMessage('전체카테고리 에서 해당 작업을 수행할 수 없습니다.')
                return;
            } else if (categoryValues[0] === 'childCategory') {
                this.showToastMessage('2차카테고리 에서 해당 작업을 수행할 수 없습니다.')
                return;
            }

            const categoryName = prompt('', '');

            if (categoryName.length <= 0 || /\s/g.test(categoryName)) {
                this.showToastMessage("비어있거나 공백이 포함 된 문자는 등록할 수 없습니다.");
                return;
            }

            this.#addCategory({
                type: 'childCategory',
                id: ++this.categoryId,
                parentId: categoryValues[1],
                seq: categoryValues[3],
                name: categoryName
            }, 'two-level');
        });

        this.deleteCategoryButton.addEventListener("click", evt => {
            const categoryValues = this.getCategoryValues(this.prevClickedButton);

            if (!categoryValues) {
                return;
            }

            // TODO
        });

        this.registerCategoryButton.addEventListener("click", evt => {
            this.showToastMessage('등록 버튼 클릭');
            // TODO
        });

        this.updateCategoryButton.addEventListener("click", evt => {
            const categoryName = prompt('', this.prevClickedButton.textContent);

            if (categoryName.length <= 0 || /\s/g.test(categoryName)) {
                this.showToastMessage("비어있거나 공백이 포함 된 문자로 변경할 수 없습니다.");
                return;
            }
            this.prevClickedButton.textContent = categoryName;
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

        if (categoryValue[1].includes('=') === true) {
            categoryValues.push(categoryValue[0]);
            categoryValue = categoryValue[1].split("=")
        }

        categoryValues.push(categoryValue[0]);
        categoryValues.push(categoryValue[1]);
        return categoryValues;
    }

    #addCategory(categoryInfo, level) {
        // categoryInfo 정보를 토대로 카테고리 추가
        switch (level) {
            case 'one-level': {
                const oneLevelCategory = document.createElement('li');
                oneLevelCategory.className = 'blog_header_category_elem';

                const categoryButton = document.createElement('button');
                categoryButton.className = 'common_button_text blog_header_category_title';
                categoryButton.setAttribute('value', `${categoryInfo['type']}:${categoryInfo['id']}&${categoryInfo['parentId']}=${categoryInfo['seq']}`);
                categoryButton.textContent = categoryInfo['name'];

                const categoryCount = document.createElement('span');
                categoryCount.className = 'category_count_text';
                categoryCount.textContent = `(0)`;

                const childCategoryList = document.createElement('ul');
                childCategoryList.className = 'blog_header_sub_category_list';

                categoryButton.appendChild(categoryCount);
                oneLevelCategory.appendChild(categoryButton);
                oneLevelCategory.appendChild(childCategoryList);
                this.categoryList.appendChild(oneLevelCategory);
                break;
            }

            case 'two-level': {
                const parentCategoryList = this.prevClickedButton.closest('li');
                const childCategoryList = parentCategoryList.getElementsByClassName('blog_header_sub_category_list');
                const childCategory = document.createElement('li');
                childCategory.className = 'blog_header_sub_category_elem';
                const categoryButton = document.createElement('button');
                categoryButton.className = 'common_button_text blog_header_sub_category_title';
                categoryButton.setAttribute('value', `${categoryInfo['type']}:${categoryInfo['id']}&${categoryInfo['parentId']}=${categoryInfo['seq']}`);
                categoryButton.textContent = categoryInfo['name'];
                const categoryCount = document.createElement('span');
                categoryCount.className = 'sub_category_count_text';
                categoryCount.textContent = `(0)`;
                childCategory.appendChild(categoryButton);
                childCategory.appendChild(categoryCount);
                childCategoryList[0].appendChild(childCategory);
                break;
            }
        }
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const categorySettingController = new CategorySettingController();
    categorySettingController.initCategorySettingController();
});