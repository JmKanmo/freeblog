class MusicPlayController extends UtilController {
    constructor() {
        super();
        this.audioPlayerContainer = document.getElementById("audioPlayerContainer");
        this.musicUtilController = new MusicUtilController();

        // music play time
        this.musicPlayReloadTimeOut = 1000;
        this.musicPlaySearchKeywordTimout = 1000;
        this.prevMusicPlaySearchKeywordTime = 0;
        this.prevMusicPlayReloadTime = 0;

        // music download time
        this.musicDownloadReloadTimeOut = 1000;
        this.musicDownloadSearchKeywordTimout = 1000;
        this.prevMusicDownloadSearchKeywordTime = 0;
        this.prevMusicDownloadReloadTime = 0;

        this.musicPlaySelectmap = new Map();
        this.musicDownloadSelectMap = new Map();

        // page size
        this.musicStoreRecordSize = 5;
        this.musicStorePageSize = 5;

        // music play
        this.musicStoreListSearchInput = document.getElementById("musicStoreListSearchInput");
        this.musicStoreListCategorySelector = document.getElementById("musicStoreListCategorySelector");
        this.musicStoreListSortSelector = document.getElementById("musicStoreListSortSelector");
        this.musicStoreListBox = document.getElementById("musicStoreListBox");
        this.musicStorePagination = document.getElementById("musicStorePagination");
        this.musicStoreListSearchButton = document.getElementById("musicStoreListSearchButton");
        this.musicSearchTypeSelector = document.getElementById("musicSearchTypeSelector");
        this.musicStoreReloadButton = document.getElementById("musicStoreReloadButton");
        this.musicStoreOptionAllSelectButton = document.getElementById("musicStoreOptionAllSelectButton");
        this.musicStoreOptionSelectCancelButton = document.getElementById("musicStoreOptionSelectCancelButton");
        this.musicStoreTakeButton = document.getElementById("musicStoreTakeButton");

        // music download
        this.musicDownloadListSearchInput = document.getElementById("musicDownloadListSearchInput");
        this.musicDownloadListCategorySelector = document.getElementById("musicDownloadListCategorySelector");
        this.musicDownloadListSortSelector = document.getElementById("musicDownloadListSortSelector");
        this.musicDownloadListBox = document.getElementById("musicDownloadListBox");
        this.musicDownloadPagination = document.getElementById("musicDownloadPagination");
        this.musicDownloadListSearchButton = document.getElementById("musicDownloadListSearchButton");
        this.musicDownloadTypeSelector = document.getElementById("musicDownloadSearchTypeSelector");
        this.musicDownloadReloadButton = document.getElementById("musicDownloadReloadButton");
        this.musicDownloadOptionAllSelectButton = document.getElementById("musicDownloadOptionAllSelectButton");
        this.musicDownloadOptionSelectCancelButton = document.getElementById("musicDownloadOptionSelectCancelButton");
        this.musicDownloadDeleteButton = document.getElementById("musicDownloadDeleteButton");
        this.isMusicDownloadFlag = false;
    }

    initMusicPlayController() {
        this.#requestMusicPlayCategoryList();
        this.#requestMusicDownloadCategoryList();
        this.initEventListener();
    }

    #requestMusicPlayCategoryList() {
        const xhr = new XMLHttpRequest();

        xhr.open("GET", `/music-category/list`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleMusicCategoryTemplate(responseValue);
                this.#requestMusicStoreList("/music/play-list");
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 카테고리 목록 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #requestMusicDownloadCategoryList() {
        const xhr = new XMLHttpRequest();

        xhr.open("GET", `/music-category/user-list`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleMusicDownloadCategoryTemplate(responseValue);
                this.#requestMusicDownloadList("/music/download-list");
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 카테고리 목록 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #requestMusicStoreList(url, page) {
        if (this.musicStoreListCategorySelector.value === null || this.musicStoreListCategorySelector.value === "") {
            return;
        }
        const musicStoreSearchKeywordValue = !this.musicStoreListSearchInput.value ? '' : this.musicStoreListSearchInput.value;
        const musicStoreListCategoryValue = !this.musicStoreListCategorySelector.value ? this.musicUtilController.TOTAL_MUSIC_CATEGORY_INDEX : this.musicStoreListCategorySelector.value;
        const musicStoreListOrderByValue = !this.musicStoreListSortSelector.value ? "ASC" : this.musicStoreListSortSelector.value;
        const musicStoreKeywordTypeValue = this.musicSearchTypeSelector.value;
        const musicStoreSearchTypeValue = `LIKE`;  // TODO 실제 서비스 시에는 FULL-TEXT 방식 고려

        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.musicStoreRecordSize, this.musicStorePageSize);

        xhr.open("GET"
            , url + '?' + queryParam.toString()
            + `&categoryId=${musicStoreListCategoryValue}&keyword=${musicStoreSearchKeywordValue}&orderBy=${musicStoreListOrderByValue}&keywordType=${musicStoreKeywordTypeValue}&searchType=${musicStoreSearchTypeValue}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue["musicPaginationResponse"]["musicDto"].length <= 0) {
                    // this.showToastMessage("해당 카테고리 내 뮤직 플레이 리스트가 비었습니다.", 100);
                    this.#handleMusicPlayTemplate(responseValue);
                    this.#clearMusicPlayPagination();
                    return;
                }

                this.#handleMusicPlayTemplate(responseValue);
                this.#clearMusicPlayPagination();
                this.#handleMusicPlayPagination(responseValue["musicPaginationResponse"]["musicPagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 목록 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #requestMusicDownloadList(url, page) {
        if (this.musicDownloadListCategorySelector.value === null || this.musicDownloadListCategorySelector.value === "") {
            return;
        }
        const musicDownloadSearchKeywordValue = !this.musicDownloadListSearchInput.value ? '' : this.musicDownloadListSearchInput.value;
        // TODO 각 사용자의 this.musicUtilController.TOTAL_MUSIC_CATEGORY_INDEX (전체 카테고리) 추가 방식,시점,필요성 고민
        // const musicDownloadListCategoryValue = !this.musicDownloadListCategorySelector.value ? this.musicUtilController.TOTAL_MUSIC_CATEGORY_INDEX : this.musicDownloadListCategorySelector.value;
        const musicDownloadListCategoryValue = this.musicDownloadListCategorySelector.value;
        const musicDownloadListOrderByValue = !this.musicDownloadListSortSelector.value ? "ASC" : this.musicDownloadListSortSelector.value;
        const musicDownloadKeywordTypeValue = this.musicDownloadTypeSelector.value;
        const musicDownloadSearchTypeValue = `LIKE`;  // TODO 실제 서비스 시에는 FULL-TEXT 방식 고려

        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.musicStoreRecordSize, this.musicStorePageSize);

        xhr.open("GET"
            , url + '?' + queryParam.toString()
            + `&categoryId=${musicDownloadListCategoryValue}&keyword=${musicDownloadSearchKeywordValue}&orderBy=${musicDownloadListOrderByValue}&keywordType=${musicDownloadKeywordTypeValue}&searchType=${musicDownloadSearchTypeValue}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue["musicPaginationResponse"]["musicDto"].length <= 0) {
                    // this.showToastMessage("해당 카테고리 내 뮤직 다운로드 리스트가 비었습니다.", 100);
                    this.#handleMusicDownloadTemplate(responseValue);
                    this.#clearMusicDownloadPagination();
                    return;
                }

                this.#handleMusicDownloadTemplate(responseValue);
                this.#clearMusicDownloadPagination();
                this.#handleMusicDownloadPagination(responseValue["musicPaginationResponse"]["musicPagination"], queryParam, url);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 다운로드 목록 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    initEventListener() {
        this.musicStorePagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestMusicStoreList(url, page);
                }
            }
            this.musicPlaySelectmap.clear();
        });

        this.musicDownloadPagination.addEventListener("click", evt => {
            const button = evt.target.closest("button");

            if (button && !button.closest("li").classList.contains("active")) {
                const url = button.getAttribute("url");
                const page = button.getAttribute("page");

                if (url && page) {
                    this.#requestMusicDownloadList(url, page);
                }
            }
            this.musicDownloadSelectMap.clear();
        });

        this.musicStoreListCategorySelector.addEventListener("change", evt => {
            this.#requestMusicStoreList("/music/play-list");
        });

        this.musicDownloadListCategorySelector.addEventListener("click", evt => {
            this.#requestMusicDownloadList("/music/download-list");
        });

        this.musicStoreListSearchInput.addEventListener("keyup", evt => {
            if (evt.keyCode == 13) {
                const current = new Date().getTime();

                if (current - this.prevMusicPlaySearchKeywordTime > this.musicPlaySearchKeywordTimout) {
                    this.#requestMusicStoreList("/music/play-list");
                    this.prevMusicPlaySearchKeywordTime = current;
                } else {
                    this.showToastMessage("잠시 후에 요청 해주세요.");
                }
            }
        });

        this.musicDownloadListSearchInput.addEventListener("keyup", evt => {
            if (evt.keyCode == 13) {
                const current = new Date().getTime();

                if (current - this.prevMusicDownloadSearchKeywordTime > this.musicDownloadSearchKeywordTimout) {
                    this.#requestMusicDownloadList("/music/download-list");
                    this.prevMusicDownloadSearchKeywordTime = current;
                } else {
                    this.showToastMessage("잠시 후에 요청 해주세요.");
                }
            }
        });

        this.musicStoreListSearchButton.addEventListener("click", evt => {
            this.#requestMusicStoreList("/music/play-list");
        });

        this.musicDownloadListSearchButton.addEventListener("click", evt => {
            this.#requestMusicDownloadList("/music/download-list");
        });

        this.musicStoreListSortSelector.addEventListener("change", evt => {
            this.#requestMusicStoreList("/music/play-list");
        });

        this.musicDownloadListSortSelector.addEventListener("change", evt => {
            this.#requestMusicDownloadList("/music/download-list");
        });


        this.musicStoreReloadButton.addEventListener("click", evt => {
            const current = new Date().getTime();

            if (current - this.prevMusicPlayReloadTime > this.musicPlayReloadTimeOut) {
                this.#requestMusicPlayCategoryList();
                this.#requestMusicStoreList("/music/play-list");
                this.prevMusicPlayReloadTime = current;
            } else {
                this.showToastMessage("잠시 후에 요청 해주세요.");
            }
        });

        this.musicDownloadReloadButton.addEventListener("click", evt => {
            const current = new Date().getTime();

            if (current - this.prevMusicDownloadReloadTime > this.musicDownloadReloadTimeOut) {
                this.#requestMusicDownloadCategoryList();
                this.#requestMusicDownloadList("/music/download-list");
                this.prevMusicDownloadReloadTime = current;
            } else {
                this.showToastMessage("잠시 후에 요청 해주세요.");
            }
        });

        this.musicStoreListBox.addEventListener("click", evt => {
            const selectedMusicBox = evt.target.closest("#musicStoreTotalBox");
            const selectedMusicCheckBox = evt.target.closest("#musicStoreCheckBox");

            if (selectedMusicBox) {
                // evt.target.closest("#musicStoreTotalBox").querySelectorAll('#musicStoreIdInput')[0].value
                const musicId = selectedMusicBox.querySelector("#musicStoreIdInput").value;
                const musicCategoryId = selectedMusicBox.querySelector("#musicStoreCategoryIdInput").value;
                const musicTitle = selectedMusicBox.querySelector("#musicStoreTitleInput").value;
                const musicArtist = selectedMusicBox.querySelector("#musicStoreArtistInput").value;
                const musicUrl = selectedMusicBox.querySelector("#musicStoreUrlInput").value;
                const musicCover = selectedMusicBox.querySelector("#musicStoreCoverInput").value;

                this.#setMusicPlayer({
                    musicId: musicId,
                    categoryId: musicCategoryId,
                    name: musicTitle,
                    artist: musicArtist,
                    url: musicUrl,
                    cover: musicCover,
                    theme: null
                });
            } else if (selectedMusicCheckBox) {
                const musicStoreTotalBox = evt.target.closest(".music_store").querySelector("#musicStoreTotalBox");

                if (musicStoreTotalBox) {
                    const musicId = musicStoreTotalBox.querySelector("#musicStoreIdInput").value;
                    const musicCategoryId = musicStoreTotalBox.querySelector("#musicStoreCategoryIdInput").value;
                    const musicTitle = musicStoreTotalBox.querySelector("#musicStoreTitleInput").value;
                    const musicArtist = musicStoreTotalBox.querySelector("#musicStoreArtistInput").value;
                    const musicUrl = musicStoreTotalBox.querySelector("#musicStoreUrlInput").value;
                    const musicCover = musicStoreTotalBox.querySelector("#musicStoreCoverInput").value;
                    const musicLrc = musicStoreTotalBox.querySelector("#musicStoreLrcInput").value;
                    let isChecked = false;

                    if (selectedMusicCheckBox.checked === true) {
                        isChecked = true;
                    } else {
                        isChecked = false;
                    }
                    const musicStoreInfoMap = new Map();
                    const key = `${musicId}&${musicCategoryId}`;

                    musicStoreInfoMap.set('musicStoreId', musicId);
                    musicStoreInfoMap.set('musicStoreCategoryId', musicCategoryId);
                    musicStoreInfoMap.set('musicStoreTitle', musicTitle);
                    musicStoreInfoMap.set('musicStoreArtist', musicArtist);
                    musicStoreInfoMap.set('musicStoreUrl', musicUrl);
                    musicStoreInfoMap.set('musicStoreCover', musicCover);
                    musicStoreInfoMap.set('musicStoreLrc', musicLrc);

                    if (isChecked === true) {
                        this.musicPlaySelectmap.set(key, musicStoreInfoMap);
                    } else {
                        this.musicPlaySelectmap.delete(key);
                    }
                }
            }
        });

        this.musicDownloadListBox.addEventListener("click", evt => {
            const selectedMusicBox = evt.target.closest("#musicDownloadTotalBox");
            const selectedMusicCheckBox = evt.target.closest("#musicDownloadCheckBox");


            if (selectedMusicBox) {
                // evt.target.closest("#musicStoreTotalBox").querySelectorAll('#musicStoreIdInput')[0].value
                const musicId = selectedMusicBox.querySelector("#musicDownloadIdInput").value;
                const musicCategoryId = selectedMusicBox.querySelector("#musicDownloadCategoryIdInput").value;
                const musicTitle = selectedMusicBox.querySelector("#musicDownloadTitleInput").value;
                const musicArtist = selectedMusicBox.querySelector("#musicDownloadArtistInput").value;
                const musicUrl = selectedMusicBox.querySelector("#musicDownloadUrlInput").value;
                const musicCover = selectedMusicBox.querySelector("#musicDownloadCoverInput").value;

                this.#setMusicPlayer({
                    musicId: musicId,
                    categoryId: musicCategoryId,
                    name: musicTitle,
                    artist: musicArtist,
                    url: musicUrl,
                    cover: musicCover,
                    theme: null
                });
            } else if (selectedMusicCheckBox) {
                const musicDownloadTotalBox = evt.target.closest(".music_download").querySelector("#musicDownloadTotalBox");

                if (musicDownloadTotalBox) {
                    const musicId = musicDownloadTotalBox.querySelector("#musicDownloadIdInput").value;
                    const musicCategoryId = musicDownloadTotalBox.querySelector("#musicDownloadCategoryIdInput").value;
                    const musicTitle = musicDownloadTotalBox.querySelector("#musicDownloadTitleInput").value;
                    const musicArtist = musicDownloadTotalBox.querySelector("#musicDownloadArtistInput").value;
                    const musicUrl = musicDownloadTotalBox.querySelector("#musicDownloadUrlInput").value;
                    const musicCover = musicDownloadTotalBox.querySelector("#musicDownloadCoverInput").value;
                    const musicLrc = musicDownloadTotalBox.querySelector("#musicDownloadLrcInput").value;
                    let isChecked = false;

                    if (selectedMusicCheckBox.checked === true) {
                        isChecked = true;
                    } else {
                        isChecked = false;
                    }
                    const musicStoreInfoMap = new Map();
                    const key = `${musicId}&${musicCategoryId}`;

                    musicStoreInfoMap.set('musicStoreId', musicId);
                    musicStoreInfoMap.set('musicStoreCategoryId', musicCategoryId);
                    musicStoreInfoMap.set('musicStoreTitle', musicTitle);
                    musicStoreInfoMap.set('musicStoreArtist', musicArtist);
                    musicStoreInfoMap.set('musicStoreUrl', musicUrl);
                    musicStoreInfoMap.set('musicStoreCover', musicCover);
                    musicStoreInfoMap.set('musicStoreLrc', musicLrc);

                    if (isChecked === true) {
                        this.musicDownloadSelectMap.set(key, musicStoreInfoMap);
                    } else {
                        this.musicDownloadSelectMap.delete(key);
                    }
                }
            }
        });

        this.musicStoreOptionAllSelectButton.addEventListener("click", evt => {
            const musicStoreListBoxArray = Array.from(this.musicStoreListBox.children);

            musicStoreListBoxArray.forEach(musicStoreListBox => {
                let isChecked = false;

                if (musicStoreListBox.tagName.toLowerCase() === 'li') {
                    const childNodeArr = Array.from(musicStoreListBox.children);
                    childNodeArr.forEach(childNode => {
                        if (childNode.tagName.toLowerCase() === 'input') {
                            if (childNode.id === "musicStoreCheckBox") {
                                // 기존에 선택된 것들은 skip
                                // if (childNode.checked === true) {
                                //     childNode.checked = false;
                                //     isChecked = false;
                                // } else {
                                //     childNode.checked = true;
                                //     isChecked = true;
                                // }
                                childNode.checked = true;
                                isChecked = true;
                            }
                        }
                        if (childNode.id === "musicStoreTotalBox") {
                            const musicTotalNodeArr = Array.from(childNode.children);
                            const musicStoreInfoMap = new Map();

                            musicTotalNodeArr.forEach(musicTotalNode => {
                                if (musicTotalNode.tagName.toLowerCase() === 'input') {
                                    if (musicTotalNode.id === 'musicStoreIdInput') {
                                        musicStoreInfoMap.set('musicStoreId', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreCategoryIdInput') {
                                        musicStoreInfoMap.set('musicStoreCategoryId', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreTitleInput') {
                                        musicStoreInfoMap.set('musicStoreTitle', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreArtistInput') {
                                        musicStoreInfoMap.set('musicStoreArtist', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreUrlInput') {
                                        musicStoreInfoMap.set('musicStoreUrl', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreCoverInput') {
                                        musicStoreInfoMap.set('musicStoreCover', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreLrcInput') {
                                        musicStoreInfoMap.set('musicStoreLrc', musicTotalNode.value);
                                    }
                                }
                            });

                            for (const [key, value] of musicStoreInfoMap) {
                                const key = `${musicStoreInfoMap.get('musicStoreId')}&${musicStoreInfoMap.get('musicStoreCategoryId')}`;

                                if (isChecked === true) {
                                    this.musicPlaySelectmap.set(key, musicStoreInfoMap);
                                } else {
                                    this.musicPlaySelectmap.delete(key);
                                }
                            }
                        }
                    });
                }
            });
        });

        this.musicDownloadOptionAllSelectButton.addEventListener("click", evt => {
            const musicDownloadListBoxArray = Array.from(this.musicDownloadListBox.children);

            musicDownloadListBoxArray.forEach(musicDownloadListBox => {
                let isChecked = false;

                if (musicDownloadListBox.tagName.toLowerCase() === 'li') {
                    const childNodeArr = Array.from(musicDownloadListBox.children);
                    childNodeArr.forEach(childNode => {
                        if (childNode.tagName.toLowerCase() === 'input') {
                            if (childNode.id === "musicDownloadCheckBox") {
                                // 기존에 선택된 것들은 skip
                                // if (childNode.checked === true) {
                                //     childNode.checked = false;
                                //     isChecked = false;
                                // } else {
                                //     childNode.checked = true;
                                //     isChecked = true;
                                // }
                                childNode.checked = true;
                                isChecked = true;
                            }
                        }
                        if (childNode.id === "musicDownloadTotalBox") {
                            const musicTotalNodeArr = Array.from(childNode.children);
                            const musicDownloadInfoMap = new Map();

                            musicTotalNodeArr.forEach(musicTotalNode => {
                                if (musicTotalNode.tagName.toLowerCase() === 'input') {
                                    if (musicTotalNode.id === 'musicDownloadIdInput') {
                                        musicDownloadInfoMap.set('musicDownloadId', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadCategoryIdInput') {
                                        musicDownloadInfoMap.set('musicDownloadCategoryId', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadTitleInput') {
                                        musicDownloadInfoMap.set('musicDownloadTitle', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadArtistInput') {
                                        musicDownloadInfoMap.set('musicDownloadArtist', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadUrlInput') {
                                        musicDownloadInfoMap.set('musicDownloadUrl', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadCoverInput') {
                                        musicDownloadInfoMap.set('musicDownloadCover', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadLrcInput') {
                                        musicDownloadInfoMap.set('musicDownloadLrc', musicTotalNode.value);
                                    }
                                }
                            });

                            for (const [key, value] of musicDownloadInfoMap) {
                                const key = `${musicDownloadInfoMap.get('musicDownloadId')}&${musicDownloadInfoMap.get('musicDownloadCategoryId')}`;

                                if (isChecked === true) {
                                    this.musicDownloadSelectMap.set(key, musicDownloadInfoMap);
                                } else {
                                    this.musicDownloadSelectMap.delete(key);
                                }
                            }
                        }
                    });
                }
            });
        });

        this.musicStoreOptionSelectCancelButton.addEventListener("click", evt => {
            const musicStoreListBoxArray = Array.from(this.musicStoreListBox.children);

            musicStoreListBoxArray.forEach(musicStoreListBox => {
                if (musicStoreListBox.tagName.toLowerCase() === 'li') {
                    const childNodeArr = Array.from(musicStoreListBox.children);
                    childNodeArr.forEach(childNode => {
                        if (childNode.tagName.toLowerCase() === 'input') {
                            if (childNode.checked === true) {
                                childNode.checked = false;
                            }
                        }
                        if (childNode.id === "musicStoreTotalBox") {
                            const musicTotalNodeArr = Array.from(childNode.children);
                            const musicStoreInfoMap = new Map();

                            musicTotalNodeArr.forEach(musicTotalNode => {
                                if (musicTotalNode.tagName.toLowerCase() === 'input') {
                                    if (musicTotalNode.id === 'musicStoreIdInput') {
                                        musicStoreInfoMap.set('musicStoreId', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreCategoryIdInput') {
                                        musicStoreInfoMap.set('musicStoreCategoryId', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreTitleInput') {
                                        musicStoreInfoMap.set('musicStoreTitle', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreArtistInput') {
                                        musicStoreInfoMap.set('musicStoreArtist', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreUrlInput') {
                                        musicStoreInfoMap.set('musicStoreUrl', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreCoverInput') {
                                        musicStoreInfoMap.set('musicStoreCover', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicStoreLrcInput') {
                                        musicStoreInfoMap.set('musicStoreLrc', musicTotalNode.value);
                                    }
                                }
                            });

                            for (const [key, value] of musicStoreInfoMap) {
                                const key = `${musicStoreInfoMap.get('musicStoreId')}&${musicStoreInfoMap.get('musicStoreCategoryId')}`;
                                this.musicPlaySelectmap.delete(key);
                            }
                        }
                    });
                }
            });
        });

        this.musicDownloadOptionSelectCancelButton.addEventListener("click", evt => {
            const musicDownloadListBoxArray = Array.from(this.musicDownloadListBox.children);

            musicDownloadListBoxArray.forEach(musicDownloadListBox => {
                if (musicDownloadListBox.tagName.toLowerCase() === 'li') {
                    const childNodeArr = Array.from(musicDownloadListBox.children);
                    childNodeArr.forEach(childNode => {
                        if (childNode.tagName.toLowerCase() === 'input') {
                            if (childNode.checked === true) {
                                childNode.checked = false;
                            }
                        }
                        if (childNode.id === "musicDownloadTotalBox") {
                            const musicTotalNodeArr = Array.from(childNode.children);
                            const musicDownloadInfoMap = new Map();

                            musicTotalNodeArr.forEach(musicTotalNode => {
                                if (musicTotalNode.tagName.toLowerCase() === 'input') {
                                    if (musicTotalNode.id === 'musicDownloadIdInput') {
                                        musicDownloadInfoMap.set('musicDownloadId', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadCategoryIdInput') {
                                        musicDownloadInfoMap.set('musicDownloadCategoryId', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadTitleInput') {
                                        musicDownloadInfoMap.set('musicDownloadTitle', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadArtistInput') {
                                        musicDownloadInfoMap.set('musicDownloadArtist', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadUrlInput') {
                                        musicDownloadInfoMap.set('musicDownloadUrl', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadCoverInput') {
                                        musicDownloadInfoMap.set('musicDownloadCover', musicTotalNode.value);
                                    } else if (musicTotalNode.id === 'musicDownloadLrcInput') {
                                        musicDownloadInfoMap.set('musicDownloadLrc', musicTotalNode.value);
                                    }
                                }
                            });

                            for (const [key, value] of musicDownloadInfoMap) {
                                const key = `${musicDownloadInfoMap.get('musicDownloadId')}&${musicDownloadInfoMap.get('musicDownloadCategoryId')}`;
                                this.musicDownloadSelectMap.delete(key);
                            }
                        }
                    });
                }
            });
        });

        this.musicStoreTakeButton.addEventListener("click", evt => {
            const musicPlaySelectMap = this.musicPlaySelectmap;
            this.#requestMusicDownload(musicPlaySelectMap);
        });

        this.musicDownloadDeleteButton.addEventListener("click", evt => {
            if (this.musicDownloadSelectMap == null || this.musicDownloadSelectMap.size <= 0) {
                this.showToastMessage("항목을 선택해주세요.");
                return;
            }

            if (confirm("해당 뮤직을 삭제하겠습니까?")) {
                const musicList = [];
                const musicDownloadSelectMap = this.musicDownloadSelectMap;

                for (const [key, value] of musicDownloadSelectMap) {
                    musicList.push({
                        musicId: value.get("musicDownloadId"),
                        musicCategoryId: value.get("musicDownloadCategoryId"),
                        title: value.get("musicDownloadTitle"),
                        artist: value.get("musicDownloadArtist"),
                        url: value.get("musicDownloadUrl"),
                        cover: value.get("musicDownloadCover"),
                        lrc: value.get("musicDownloadLrc")
                    });
                }

                const xhr = new XMLHttpRequest();
                xhr.open("DELETE", `/music/delete`, true);
                xhr.setRequestHeader("Content-Type", "application/json");

                xhr.addEventListener("loadend", event => {
                    let status = event.target.status;
                    const responseValue = JSON.parse(event.target.responseText);

                    if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                        this.showToastMessage(responseValue["message"]);
                    } else {
                        this.#requestMusicDownloadList("/music/download-list");
                    }
                });

                xhr.addEventListener("error", event => {
                    this.showToastMessage("뮤직 삭제에 실패했습니다.");
                });
                xhr.send(JSON.stringify(musicList));
            }
        });
    }

    #requestMusicDownload(musicPlaySelectMap) {
        if (this.isMusicDownloadFlag === true) {
            this.showToastMessage("뮤직 다운로드를 진행 중입니다.");
            return;
        }
        const musicList = [];

        for (const [key, value] of musicPlaySelectMap) {
            musicList.push({
                musicId: value.get("musicStoreId"),
                musicCategoryId: value.get("musicStoreCategoryId"),
                title: value.get("musicStoreTitle"),
                artist: value.get("musicStoreArtist"),
                url: value.get("musicStoreUrl"),
                cover: value.get("musicStoreCover"),
                lrc: value.get("musicStoreLrc")
            });
        }

        const xhr = new XMLHttpRequest();

        xhr.open("POST", `/music/download`, true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.showToastMessage("뮤직 플레이 리스트를 담았습니다.");
                this.#requestMusicDownloadCategoryList();
                this.#requestMusicDownloadList("/music/download-list");
            }
            this.isMusicDownloadFlag = false;
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 플레이 리스트를 담기에 실패했습니다.");
            this.isMusicDownloadFlag = false;
        });
        xhr.send(JSON.stringify(musicList));
        this.isMusicDownloadFlag = true;
    }

    #setMusicPlayer(musicStoreMap) {
        const musicTestPlayerElement = document.createElement('div');
        musicTestPlayerElement.id = `audio_player`;
        this.audioPlayerContainer.appendChild(musicTestPlayerElement);

        const musicMap = new Map();
        const musicCategoryMap = new Map();
        const musicConfigMap = new Map();

        const musicPlayerId = `${musicStoreMap["categoryId"]}&${musicStoreMap["musicId"]}`;

        // 기본 값 지정
        musicConfigMap.set('config', {
            listFolded: true,
            listMaxHeight: 90,
            lrcType: 0,
            autoplay: false,
            mutex: true,
            order: 'random',
            mode: {
                fixed: true,
                mini: false
            }
        });

        musicCategoryMap.set(musicPlayerId, {
            audio: [
                {
                    name: musicStoreMap["name"],
                    artist: musicStoreMap["artist"],
                    url: musicStoreMap["url"],
                    cover: musicStoreMap["cover"],
                    theme: musicStoreMap["theme"],
                }
            ]
        });

        musicMap.set('data', musicCategoryMap);
        musicMap.set('config', musicConfigMap.get('config'));

        this.musicUtilController.clearAudioPlayer();
        this.musicUtilController.initAudioPlayer(musicMap);
        this.musicUtilController.playAudioPlayer(musicPlayerId);
    }

    #handleMusicCategoryTemplate(responseValue) {
        const musicCategoryTemplate = document.getElementById("music-category-template").innerHTML;
        const musicCategoryTemplateObject = Handlebars.compile(musicCategoryTemplate);
        const jsonObj = responseValue["musicPaginationResponse"];
        const musicTemplateHTML = musicCategoryTemplateObject({"musicCategoryList": jsonObj});
        this.musicStoreListCategorySelector.innerHTML = musicTemplateHTML;
    }

    #handleMusicDownloadCategoryTemplate(responseValue) {
        const musicCategoryTemplate = document.getElementById("music-download-category-template").innerHTML;
        const musicCategoryTemplateObject = Handlebars.compile(musicCategoryTemplate);
        const jsonObj = responseValue["musicPaginationResponse"];
        const totalMusicTemplateHTML = `<option value="0">전체</option>`;
        const musicTemplateHTML = musicCategoryTemplateObject({"musicCategoryList": jsonObj});
        this.musicDownloadListCategorySelector.innerHTML = (totalMusicTemplateHTML + musicTemplateHTML);
    }

    #handleMusicPlayTemplate(responseValue) {
        const musicTemplate = document.getElementById("music-template").innerHTML;
        const musicTemplateObject = Handlebars.compile(musicTemplate);
        const jsonObj = responseValue["musicPaginationResponse"]["musicDto"];
        const musicTemplateHTML = musicTemplateObject({"musicList": jsonObj});
        this.musicStoreListBox.innerHTML = musicTemplateHTML;
    }

    #handleMusicDownloadTemplate(responseValue) {
        const musicTemplate = document.getElementById("music-download-template").innerHTML;
        const musicTemplateObject = Handlebars.compile(musicTemplate);
        const jsonObj = responseValue["musicPaginationResponse"]["musicDto"];
        const musicTemplateHTML = musicTemplateObject({"musicList": jsonObj});
        this.musicDownloadListBox.innerHTML = musicTemplateHTML;
    }

    #clearMusicPlayPagination() {
        this.musicStorePagination.innerHTML = ``;
    }

    #clearMusicDownloadPagination() {
        this.musicDownloadPagination.innerHTML = ``;
    }

    #handleMusicPlayPagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.musicStorePagination.innerHTML = '';
            return;
        }
        this.musicStorePagination.innerHTML = this.drawBasicPagination(pagination, queryParam, url);
    }

    #handleMusicDownloadPagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.musicDownloadPagination.innerHTML = '';
            return;
        }
        this.musicDownloadPagination.innerHTML = this.drawBasicPagination(pagination, queryParam, url);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const musicPlayController = new MusicPlayController();
    musicPlayController.initMusicPlayController();
});