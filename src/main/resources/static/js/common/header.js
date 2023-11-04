class HeaderController extends UtilController {
    constructor() {
        super();
        this.dayNightSkinButton = document.getElementById("day_night_skin_button");
        this.likePostButton = document.getElementById("like_post_button");
        this.userOptionDiv = document.getElementById("user_option_div");
        this.userProfileBtn = document.getElementById("user_profile_button");
        this.headerProfileImage = document.getElementById("header_profile_image");
        this.noticeButton = document.getElementById("notice_button");
        this.userLikePostBlockCloseButton = document.getElementById("user_like_post_block_close_button");
        this.userLikePostBlockReloadButton = document.getElementById("user_like_post_block_reload_button");
        this.userLikePostNoticeText = document.getElementById("user_like_post_notice_text");
        this.userLikePostContainer = document.getElementById("user_like_post_container");
        this.userLikePostBlockDeleteAllButton = document.getElementById("user_like_post_block_delete_all_button");
        this.userLikePostTemplateHTML = null;
        this.lastSearchTime = null;
        this.noticeAlarmBoard = document.getElementById("notice_alarm_dot_board");
        this.isReadNoticeAlarm = false;
        this.noticeContainer = document.getElementById("notice_container");
        this.noticeTemplateContainer = document.getElementById("notice_template_container");
        this.noticeListContainer = document.getElementById("notice_list_container");
        this.noticeCloseButton = document.getElementById("notice_close_button");
        this.noticePagination = document.getElementById("noticePagination");
        this.noticeTemplateFlag = false;

        this.audioPlayerSettingButton = document.getElementById("audioPlayerSettingButton");
        this.musicPlayerConfigBox = document.getElementById("musicPlayerConfigBox");
        this.musicPlayerConfigForm = document.getElementById("musicPlayerConfigForm");
        this.musicStoreButton = document.getElementById("musicStoreButton");
        this.musicConfigSaveButton = document.getElementById("musicConfigSaveButton");

        this.musicHeaderController = new MusicHeaderController();
        this.noticeRecordSize = 5;
        this.noticePageSize = 5;
    }

    initHeaderController() {
        this.initCheckNotice();
        this.initEventHandler();
    }

    initCheckNotice() {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", "/notice/check-alarm", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", evt => {
            const status = evt.target.status;
            const responseValue = JSON.parse(evt.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                if (responseValue === true) {
                    if (this.noticeAlarmBoard) {
                        this.noticeAlarmBoard.style.display = 'block';
                    }
                }
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 공지사항 알림 정보를 불러오지 못했습니다.');
        });

        xhr.send();
    }

    initEventHandler() {
        if (this.userLikePostBlockCloseButton != null) {
            this.userLikePostBlockCloseButton.addEventListener("click", evt => {
                if (this.userLikePostContainer != null) {
                    this.userLikePostContainer.style.display = "none";
                    this.userLikePostBlockCloseButton.style.visibility = 'hidden';
                    this.userLikePostBlockReloadButton.style.visibility = "hidden";
                    this.userLikePostNoticeText.style.visibility = "hidden";
                    this.userLikePostBlockDeleteAllButton.style.visibility = "hidden";
                }
            });
        }

        if (this.userLikePostBlockReloadButton != null) {
            this.userLikePostBlockReloadButton.addEventListener("click", evt => {
                if (this.lastSearchTime == null) {
                    this.#requestUserLikePostInfo();
                    this.lastSearchTime = Date.now();
                } else {
                    const now = Date.now();
                    const diff = Date.now() - this.lastSearchTime;
                    // 5초에 한번씩 클릭
                    if (diff > (1000 * 5)) {
                        this.#requestUserLikePostInfo();
                        this.lastSearchTime = now;
                    } else {
                        this.showToastMessage("잠시후에 다시 시도 해주세요.")
                    }
                }
            });
        }

        if (this.dayNightSkinButton != null) {
            this.dayNightSkinButton.addEventListener("click", evt => {
                const img = evt.target.closest("img");
                if (img.src.includes('sun')) {
                    img.src = '/images/night.png'
                } else if (img.src.includes('night')) {
                    img.src = '/images/sun.gif';
                }
            });
        }

        if (this.likePostButton != null) {
            this.likePostButton.addEventListener("click", evt => {
                if (this.userLikePostContainer != null) {
                    const display = this.userLikePostContainer.style.display;

                    if (display === "" || display === "none") {
                        if (this.noticeContainer != null && this.noticeContainer.style.display === 'block') {
                            this.noticeContainer.style.display = 'none';
                        }
                        if (!this.userLikePostTemplateHTML) {
                            this.#requestUserLikePostInfo();
                        } else {
                            this.userLikePostContainer.style.display = "block";
                            this.userLikePostBlockCloseButton.style.visibility = "visible";
                            this.userLikePostBlockReloadButton.style.visibility = "visible";
                            this.userLikePostNoticeText.style.visibility = "visible";
                            this.userLikePostBlockDeleteAllButton.style.visibility = "visible";
                            this.userLikePostContainer.innerHTML = this.userLikePostTemplateHTML;
                            this.initUserLikePostDeleteEvent();
                        }
                    } else {
                        this.userLikePostContainer.style.display = "none";
                        this.userLikePostBlockCloseButton.style.visibility = "hidden";
                        this.userLikePostBlockReloadButton.style.visibility = "hidden";
                        this.userLikePostNoticeText.style.visibility = "hidden";
                        this.userLikePostBlockDeleteAllButton.style.visibility = "hidden";
                    }
                }
            });
        }

        if (this.noticeButton != null) {
            this.noticeButton.addEventListener("click", evt => {
                this.#requestNoticeAlarmRead();
                this.displayNoticeView();
            });
        }

        if (this.userProfileBtn != null) {
            this.userProfileBtn.addEventListener("click", evt => {
                if (this.userOptionDiv != null) {
                    if (this.userOptionDiv.style.display === '' || this.userOptionDiv.style.display === 'none') {
                        this.userOptionDiv.style.display = 'block';
                    } else {
                        this.userOptionDiv.style.display = 'none';
                    }
                }
            });
        }

        if (this.userLikePostBlockDeleteAllButton != null) {
            this.userLikePostBlockDeleteAllButton.addEventListener("click", evt => {
                if (confirm("게시글 정보를 모두 삭제하겠습니까?")) {
                    const userLikePostList = document.getElementById("user_like_post_list");
                    const userId = this.userLikePostBlockDeleteAllButton.value;
                    const xhr = new XMLHttpRequest();

                    // /post/all-user-list
                    xhr.open('DELETE', `/like/post/all-user-list?userId=${userId}`, true);

                    xhr.addEventListener("loadend", event => {
                        let status = event.target.status;
                        const responseValue = event.target.responseText;

                        if ((status >= 400 && status <= 500) || (status > 500)) {
                            this.showToastMessage(responseValue);
                        } else if (status == 200) {
                            if (userLikePostList != null) {
                                userLikePostList.innerHTML = ``;
                            }
                        }
                    });

                    xhr.addEventListener("error", event => {
                        this.showToastMessage(`게시글 정보 삭제에 실패하였습니다.`);
                    });

                    xhr.send();
                }
            });
        }

        if (this.noticeCloseButton != null) {
            this.noticeCloseButton.addEventListener("click", evt => {
                this.noticeContainer.style.display = 'none';
            });
        }

        if (this.noticePagination != null) {
            this.noticePagination.addEventListener("click", evt => {
                const button = evt.target.closest("button");

                if (button && !button.closest("li").classList.contains("active")) {
                    const url = button.getAttribute("url");
                    const page = button.getAttribute("page");

                    if (url && page) {
                        this.#requestNoticeList(url, page);
                    }
                }
            });
        }

        if (this.audioPlayerSettingButton != null) {
            this.audioPlayerSettingButton.addEventListener("click", evt => {
                const xhr = new XMLHttpRequest();
                xhr.open("GET", "/music/config", true);

                xhr.addEventListener("loadend", evt => {
                    const status = evt.target.status;
                    const responseValue = JSON.parse(evt.target.responseText);

                    if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                        this.showToastMessage(responseValue["message"]);
                    } else {
                        const musicPlayerConfigTemplate = document.getElementById("music-config-template").innerHTML;
                        const musicPlayerConfigTemplateObject = Handlebars.compile(musicPlayerConfigTemplate);
                        const musicPlayerConfigTemplateObjectHTML = musicPlayerConfigTemplateObject(responseValue["musicPaginationResponse"]);
                        this.musicPlayerConfigBox.innerHTML = musicPlayerConfigTemplateObjectHTML;
                    }
                });

                xhr.addEventListener("error", event => {
                    this.showToastMessage('오류가 발생하여 뮤직 설정을 읽는데 실패하였습니다.');
                });
                xhr.send();
            });
        }

        if (this.musicConfigSaveButton != null) {
            this.musicConfigSaveButton.addEventListener("click", evt => {
                const xhr = new XMLHttpRequest();
                xhr.open("POST", "/music/config_save", true);

                xhr.addEventListener("loadend", evt => {
                    const status = evt.target.status;
                    const responseValue = JSON.parse(evt.target.responseText);

                    if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                        this.showToastMessage(responseValue["message"]);
                    } else {
                        location.reload();
                    }
                });

                xhr.addEventListener("error", event => {
                    this.showToastMessage('오류가 발생하여 뮤직 설정 저장에 실패하였습니다.');
                });

                // music config data setting
                document.getElementById("listFoldedHiddenInput").value = document.getElementById("listFoldedInput").checked;
                document.getElementById("listMaxHeightHiddenInput").value = !document.getElementById("listMaxHeightInput").value ? 0 : document.getElementById("listMaxHeightInput").value;
                document.getElementById("autoPlayHiddenInput").value = false; // document.getElementById("autoPlayInput").checked;
                document.getElementById("duplicatePlayHiddenInput").value = document.getElementById("duplicatePlayInput").checked;
                document.getElementById("playOrderHiddenInput").value = document.getElementById("playOrderInput").value;
                document.getElementById("playModeHiddenInput").value = document.getElementById("playModeInput").value;
                xhr.send(new FormData(this.musicPlayerConfigForm));
            });
        }

        if (this.musicStoreButton != null) {
            this.musicStoreButton.addEventListener("click", evt => {
                this.openPopUp(1080, 750, '/music/store', 'popup');
            });
        }

        /**
         * 추후에 JS es6 코드로 대체 ...
         */
        $('body').click(function (e) {
            if (!$('#user_option_div').has(e.target).length) {
                const button = e.target.closest("button");
                if (button == null || button.className !== "user_profile_button") {
                    $('#user_option_div').hide();
                }
            }
        });
    }

    #requestNoticeAlarmRead() {
        if (this.isReadNoticeAlarm === false && this.noticeAlarmBoard && this.noticeAlarmBoard.style.display === 'block') {
            const xhr = new XMLHttpRequest();
            xhr.open("POST", "/notice/read-alarm", true);
            xhr.setRequestHeader("Content-Type", "application/json");

            xhr.addEventListener("loadend", evt => {
                const status = evt.target.status;
                const responseValue = JSON.parse(evt.target.responseText);

                if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                    this.isReadNoticeAlarm = false;
                    this.showToastMessage(responseValue["message"]);
                } else {
                    if (this.noticeAlarmBoard) {
                        this.noticeAlarmBoard.style.display = 'none';
                    }
                }
            });

            xhr.addEventListener("error", event => {
                this.showToastMessage('오류가 발생하여 공지사항 알림을 읽는데 실패하였습니다.');
                this.isReadNoticeAlarm = false;
            });

            xhr.send();
            this.isReadNoticeAlarm = true;
        }
    }

    displayNoticeView() {
        if (this.noticeContainer != null) {
            if (this.noticeContainer.style.display === '' || this.noticeContainer.style.display === 'none') {
                if (this.userLikePostContainer != null && this.userLikePostContainer.style.display === 'block') {
                    this.userLikePostContainer.style.display = "none";
                    this.userLikePostBlockCloseButton.style.visibility = 'hidden';
                    this.userLikePostBlockReloadButton.style.visibility = "hidden";
                    this.userLikePostNoticeText.style.visibility = "hidden";
                    this.userLikePostBlockDeleteAllButton.style.visibility = "hidden";
                }
                this.noticeContainer.style.display = 'block';

                if (this.noticeTemplateFlag == false) {
                    this.#requestNoticeList("/notice/search-list");
                }
            } else {
                this.noticeContainer.style.display = 'none';
            }
        }
    }

    #requestNoticeList(url, page) {
        const xhr = new XMLHttpRequest();
        const queryParam = this.getQueryParam(page, this.noticeRecordSize, this.noticePageSize);

        xhr.open("GET", url + '?' + queryParam.toString(), true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", evt => {
            const status = evt.target.status;
            const responseValue = JSON.parse(evt.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleNoticeTemplateList(responseValue);
                this.#clearNoticePagination();
                this.#handleNoticePagination(responseValue["noticePaginationResponse"]["noticePagination"], queryParam, url);
                this.noticeTemplateFlag = true;
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 공지사항 리스트 정보를 불러오지 못했습니다.');
        });

        xhr.send();
    }

    #requestUserLikePostInfo() {
        const xhr = new XMLHttpRequest();
        xhr.open("GET", "/like/post/user-list", true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.addEventListener("loadend", evt => {
            const status = evt.target.status;
            const responseValue = JSON.parse(evt.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.userLikePostContainer.style.display = "block";
                this.userLikePostBlockCloseButton.style.visibility = "visible";
                this.userLikePostBlockReloadButton.style.visibility = "visible";
                this.userLikePostNoticeText.style.visibility = "visible";
                this.userLikePostBlockDeleteAllButton.style.visibility = "visible";
                this.#handlePostLikeTemplateList(responseValue);
                this.initUserLikePostDeleteEvent();
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage('오류가 발생하여 사용자가 좋아요 누른 게시글 정보를 불러오지 못했습니다.');
        });

        xhr.send();
    }

    removeHeaderUserProfileImage() {
        this.headerProfileImage.src = this.getDefaultUserProfileThumbnail();
    }

    setHeaderUserProfileImage(src) {
        this.headerProfileImage.src = src;
    }

    #handlePostLikeTemplateList(responseValue) {
        const userLikePostTemplate = document.getElementById("user-like-post-template").innerHTML;
        const userLikePostTemplateObject = Handlebars.compile(userLikePostTemplate);
        const userLikePostTemplateHTML = userLikePostTemplateObject({"userLikePostList": responseValue["userLikePostInnerList"]});
        this.userLikePostContainer.innerHTML = userLikePostTemplateHTML;
        this.userLikePostTemplateHTML = userLikePostTemplateHTML;
    }

    #handleNoticeTemplateList(responseValue) {
        const noticeTemplate = document.getElementById("notice-template").innerHTML;
        const noticeTemplateObject = Handlebars.compile(noticeTemplate);
        const noticeTemplateHTML = noticeTemplateObject({"noticeDtoList": responseValue["noticePaginationResponse"]["noticeDto"]});
        this.noticeListContainer.innerHTML = noticeTemplateHTML;
    }

    #clearNoticePagination() {
        this.noticePagination.innerHTML = ``;
    }

    #handleNoticePagination(pagination, queryParam, url) {
        if (!pagination || !queryParam) {
            this.noticePagination.innerHTML = '';
            return;
        }
        this.noticePagination.innerHTML = this.drawSimplePagination(pagination, queryParam, url);
    }

    initUserLikePostDeleteEvent() {
        const userLikePostList = document.getElementById("user_like_post_list");

        userLikePostList.addEventListener("click", evt => {
            const button = evt.target.closest("button");
            const list = evt.target.closest("li");

            if (list != null && button != null) {
                if (confirm("게시글 정보를 삭제 하겠습니까?")) {
                    const values = button.value.split("&");
                    const userId = values[0].split("=")[1];
                    const postId = values[1].split("=")[1];
                    const xhr = new XMLHttpRequest();

                    xhr.open('DELETE', `/like/post/user-list?userId=${userId}&postId=${postId}`, true);

                    xhr.addEventListener("loadend", event => {
                        let status = event.target.status;
                        const responseValue = event.target.responseText;

                        if ((status >= 400 && status <= 500) || (status > 500)) {
                            this.showToastMessage(responseValue);
                        } else if (status == 200) {
                            list.remove();
                        }
                    });

                    xhr.addEventListener("error", event => {
                        this.showToastMessage(`게시글 정보 삭제에 실패하였습니다.`);
                    });

                    xhr.send();
                }
            }
        });
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // 추후에 로드 시에, 서버에서 전달받은 데이터 로드 메서드 호출
    const headerController = new HeaderController();
    headerController.initHeaderController();
});