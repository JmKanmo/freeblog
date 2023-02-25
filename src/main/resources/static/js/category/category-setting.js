class CategorySettingController extends UtilController {
    constructor() {
        super();
        this.oneLevelCategoryButton = document.getElementById("one_level_category_button");
        this.twoLevelCategoryButton = document.getElementById("two_level_category_button");
        this.updateCategoryButton = document.getElementById("update_category_button");
        this.deleteCategoryButton = document.getElementById("delete_category_button");
        this.categoryList = document.getElementById("blog_header_category_list");
        this.prevClickedButton = null;
        this.categoryCountText = document.getElementById("category_count_text");
        this.registerCategoryButton = document.getElementById("register_category_button");
        this.categoryId = document.getElementById("categoryId").value;
        this.categorySeq = document.getElementById("categorySeq").value;
        this.blogId = document.getElementById("blogIdInput").value;
        this.isSubmitFlag = false;
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

            if (this.checkCategoryName(categoryName)) {
                return;
            }

            this.#addCategory({
                type: 'parentCategory',
                id: ++this.categoryId,
                parentId: 0,
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

            if (this.checkCategoryName(categoryName)) {
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
            } else if (categoryValues === "totalCategory") {
                this.showToastMessage('해당 카테고리는 삭제할 수 없습니다.');
                return;
            }

            if (categoryValues[0] === "parentCategory") {
                const childCategory = this.prevClickedButton.closest('li');
                const childCategoryList = childCategory.getElementsByClassName('blog_header_sub_category_list')[0];
                const childCategoryCount = childCategoryList.childElementCount;

                if (childCategoryCount > 0) {
                    if (confirm('하위 카테고리 및 포함된 게시글을 모두 삭제하겠습니까?')) {
                        const childCategories = childCategoryList.getElementsByTagName('li');
                        const loop = childCategories.length;

                        for (let idx = 0; idx < loop; idx++) {
                            this.#updateCategoryCount(childCategories[0]);
                        }
                        this.#updateCategoryCount(childCategory);
                        this.prevClickedButton = null;
                    }
                } else {
                    if (confirm('해당 카테고리 및 포함된 게시글을 모두 삭제하겠습니까?')) {
                        this.#updateCategoryCount(childCategory);
                        this.prevClickedButton = null;
                    }
                }
            } else if (categoryValues[0] === "childCategory") {
                if (confirm('해당 카테고리 및 포함된 게시글을 모두 삭제하겠습니까?')) {
                    const childCategory = this.prevClickedButton.closest('li');
                    this.#updateCategoryCount(childCategory);
                    this.prevClickedButton = null;
                }
            }
        });

        this.registerCategoryButton.addEventListener("click", evt => {
            if (confirm("카테고리를 등록하겠습니까?")) {
                if (this.isSubmitFlag === true) {
                    this.showToastMessage("카테고리를 등록 중입니다.");
                    return;
                }

                const registeredCategory = this.getRegisteredCategoryList();
                const xhr = new XMLHttpRequest();

                xhr.open("POST", `/category/register/${this.blogId}`, true);
                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.addEventListener("loadend", event => {
                    let status = event.target.status;
                    const responseValue = JSON.parse(event.target.responseText);

                    if ((status >= 400 && status <= 500) || (status > 500)) {
                        this.showToastMessage(responseValue["message"]);
                    } else {
                        this.showToastMessage("카테고리가 등록되었습니다. 페이지를 새로고침해주세요.");
                    }
                    this.isSubmitFlag = false;
                });

                xhr.addEventListener("error", event => {
                    this.showToastMessage("카테고리 정보를 등록하는데 실패하였습니다.");
                    this.isSubmitFlag = false;
                });

                xhr.send(
                    JSON.stringify(registeredCategory)
                );
                this.isSubmitFlag = true;
            }
        });

        this.updateCategoryButton.addEventListener("click", evt => {
            if (!this.prevClickedButton) {
                this.showToastMessage('대상 카테고리가 선택되지 않았습니다.');
                return;
            } else if (this.prevClickedButton.id === 'total_category_search_title') {
                this.showToastMessage('해당 카테고리는 수정할 수 없습니다.');
                return;
            }

            const categoryName = prompt('', this.prevClickedButton.textContent);

            if (this.checkCategoryName(categoryName)) {
                return;
            }
            this.prevClickedButton.textContent = categoryName;
        });
    }

    getRegisteredCategoryList() {
        const registeredCategoryList = [];
        const categories = this.categoryList.childNodes;

        if (categories.length <= 1) {
            this.showToastMessage('등록된 카테고리가 없습니다.');
            return;
        }

        for (let i = 0; i < categories.length; i++) {
            if (categories[i].nodeName !== "#text") {
                const category = categories[i];
                const categoryButton = category.getElementsByTagName('button')[0];
                const categoryValue = categoryButton.value;

                if (categoryValue !== 'totalCategory') {
                    const categoryValues = this.getCategoryValues(categoryValue, true);
                    registeredCategoryList.push({
                        type: categoryValues[0],
                        id: categoryValues[1],
                        parentId: 0,
                        seq: categoryValues[3],
                        name: categoryButton.innerText
                    });
                    if (category.getElementsByTagName('ul')[0].childNodes.length > 0) {
                        const childCategories = category.getElementsByTagName('ul')[0].childNodes;

                        for (let j = 0; j < childCategories.length; j++) {
                            if (childCategories[j].nodeName !== "#text") {
                                const childCategory = childCategories[j];
                                const childCategoryButton = childCategory.getElementsByTagName('button')[0];
                                const childCategoryValue = childCategoryButton.value;
                                const childCategoryValues = this.getCategoryValues(childCategoryValue, true);
                                registeredCategoryList.push({
                                    type: childCategoryValues[0],
                                    id: childCategoryValues[1],
                                    parentId: categoryValues[1],
                                    seq: childCategoryValues[3],
                                    name: childCategoryButton.innerText
                                });
                            }
                        }
                    }
                }
            }
        }
        return registeredCategoryList;
    }

    getCategoryValues(category, isJson) {
        if (!category) {
            this.showToastMessage('대상 카테고리가 선택되지 않았습니다.');
            return null;
        }

        let categoryValue = !isJson ? category.value : category;

        if (!categoryValue && !isJson) {
            this.showToastMessage('대상 카테고리 유효성 검사에 실패하였습니다.');
            return null;
        }

        if (categoryValue === 'totalCategory') {
            return categoryValue;
        }

        categoryValue = categoryValue.split(":");

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
                categoryCount.textContent = ` (0)`;

                const childCategoryList = document.createElement('ul');
                childCategoryList.className = 'blog_header_sub_category_list';

                oneLevelCategory.appendChild(categoryButton);
                oneLevelCategory.appendChild(categoryCount);
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
                categoryCount.textContent = ` (0)`;
                childCategory.appendChild(categoryButton);
                childCategory.appendChild(categoryCount);
                childCategoryList[0].appendChild(childCategory);
                break;
            }
        }
    }

    checkCategoryName(categoryName) {
        if (categoryName.length <= 0 || /\s/g.test(categoryName)) {
            this.showToastMessage("비어있거나 공백이 포함 된 문자로 변경할 수 없습니다.");
            return true;
        } else if (categoryName.length > 25) {
            this.showToastMessage("카테고리명은 25 글자를 넘을 수 없습니다.");
            return true;
        }
        return false;
    }

    #updateCategoryCount(targetCategory) {
        const targetCategoryCount = parseInt(targetCategory.innerText.split('(')[1].split(')')[0]);
        const totalCategoryCount = parseInt(this.categoryCountText.innerText.split('(')[1].split(')')[0]);
        const result = totalCategoryCount - targetCategoryCount;
        this.categoryCountText.innerText = `(${result < 0 ? 0 : result})`;
        targetCategory.remove();
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const categorySettingController = new CategorySettingController();
    categorySettingController.initCategorySettingController();
});