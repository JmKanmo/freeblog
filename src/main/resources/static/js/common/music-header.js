class MusicHeaderController extends UtilController {
    constructor() {
        super();
        this.musicUtilController = new MusicUtilController();
        this.audioPlayerCategoryList = document.getElementById("audioPlayerCategoryList");
        this.blogIdHiddenInput = document.getElementById("blogIdHiddenInput");
        this.TOTAL_CATEGORY_INDEX = Number.MAX_SAFE_INTEGER;
    }

    initMusicPlayer() {
        this.#requestMusicCategory();
        this.initEventListener();
    }

    initEventListener() {
        if (this.audioPlayerCategoryList) {
            this.audioPlayerCategoryList.addEventListener("change", evt => {
                this.#requestMusic(this.audioPlayerCategoryList.value);
            });
        }
    }

    reloadMusicPlayer() {
        if (this.audioPlayerCategoryList) {
            this.#requestMusic(this.audioPlayerCategoryList.value);
        }
    }

    #requestMusicCategory() {
        // 뮤직 카테고리, 뮤직, 뮤직 설정 정보 로드
        if (!this.blogIdHiddenInput) {
            return;
        }
        const xhr = new XMLHttpRequest();
        const blogId = !this.blogIdHiddenInput.value ? 0 : this.blogIdHiddenInput.value;

        xhr.open("GET", `/music-category/open/user-list/${blogId}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleMusicCategoryTemplate(responseValue);
                this.#requestMusic();
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 카테고리 목록 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #requestMusic(categoryId) {
        const urlSearchParam = new URLSearchParams({
            blogId: !this.blogIdHiddenInput ? 0 : this.blogIdHiddenInput.value,
            categoryId: !categoryId ? 0 : categoryId,
            orderBy: `ASC`
            // TODO 필요에 따라 keyword, searchType 등등 추가로 정의
        }).toString();

        const xhr = new XMLHttpRequest();
        const blogId = !this.blogIdHiddenInput.value ? 0 : this.blogIdHiddenInput.value;

        xhr.open("GET", `/music/open/play-list/${blogId}?${urlSearchParam}`, true);

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = JSON.parse(event.target.responseText);

            if (((status >= 400 && status <= 500) || (status > 500)) || (status > 500)) {
                this.showToastMessage(responseValue["message"]);
            } else {
                this.#handleMusicTemplate(responseValue);
            }
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage("뮤직 정보를 불러오는데 실패하였습니다.");
        });
        xhr.send();
    }

    #handleMusicTemplate(responseValue) {
        const musicResponse = responseValue["musicPaginationResponse"]["userMusicDtoList"];
        const musicConfigResponse = responseValue["musicPaginationResponse"]["userMusicConfigDto"];
        const musicMap = new Map();
        const musicCategoryMap = new Map();
        const musicConfigMap = new Map();
        const totalMusicList = [];

        // 기본 값 지정, 추후에 DB 저장 및 페이지 구성을 통해 관리
        musicConfigMap.set('config', {
            listFolded: musicConfigResponse["listFolded"],
            listMaxHeight: musicConfigResponse["listMaxHeight"],
            lrcType: 0,
            autoplay: musicConfigResponse["autoPlay"],
            mutex: musicConfigResponse["duplicatePlay"],
            order: musicConfigResponse["playOrder"] ? musicConfigResponse["playOrder"].toLowerCase() : "list",
            mode: musicConfigResponse["playMode"] ? {
                fixed: musicConfigResponse["playMode"] && musicConfigResponse["playMode"].toLowerCase() === 'fixed' ? true : false,
                mini: musicConfigResponse["playMode"] && musicConfigResponse["playMode"].toLowerCase() === 'mini' ? true : false
            } : "fixed"
            ,
            loop: musicConfigResponse["loopMode"] ? musicConfigResponse["loopMode"].toLowerCase() : 'all',
            immediatelyStart: true // 페이지 로드 시에 바로 재생
        });

        musicResponse.forEach(music => {
            const categoryId = music["categoryId"];
            const mapValue = musicCategoryMap.get(categoryId);

            if (mapValue) {
                const audioList = mapValue["audio"];
                audioList.push({
                    name: music["name"],
                    artist: music["artist"],
                    url: music["url"],
                    cover: music["cover"],
                    theme: music["theme"]
                });
            } else {
                musicCategoryMap.set(categoryId, {
                    audio: [
                        {
                            name: music["name"],
                            artist: music["artist"],
                            url: music["url"],
                            cover: music["cover"],
                            theme: music["theme"],
                        }
                    ]
                });
            }
            totalMusicList.push({
                name: music["name"],
                artist: music["artist"],
                url: music["url"],
                cover: music["cover"],
                theme: music["theme"]
            });
        });
        musicCategoryMap.set(this.TOTAL_CATEGORY_INDEX, {audio: totalMusicList});

        musicMap.set('data', musicCategoryMap);
        musicMap.set('config', musicConfigMap.get('config'));

        this.musicUtilController.clearAudioPlayer();
        this.musicUtilController.initAudioPlayer(musicMap);

        // if (musicConfigMap.get("config")["immediatelyStart"] == true) {
        //     this.musicUtilController.autoPlayAudioPlayer(this.TOTAL_CATEGORY_INDEX, 0);
        // }
    }

    #handleMusicCategoryTemplate(responseValue) {
        const musicCategoryTemplate = document.getElementById("music-download-category-template").innerHTML;
        const musicCategoryTemplateObject = Handlebars.compile(musicCategoryTemplate);
        const jsonObj = responseValue["musicPaginationResponse"];
        const totalMusicTemplateHTML = `<option value=${this.TOTAL_CATEGORY_INDEX}>전체</option>`;
        const musicTemplateHTML = musicCategoryTemplateObject({"musicCategoryList": jsonObj});
        this.audioPlayerCategoryList.innerHTML = (totalMusicTemplateHTML + musicTemplateHTML);
    }
}