class MusicHeaderController extends UtilController {
    constructor() {
        super();
        this.musicUtilController = new MusicUtilController();
        this.audioPlayerCategoryList = document.getElementById("audioPlayerCategoryList");
        this.blogIdHiddenInput = document.getElementById("blogIdHiddenInput");
    }

    initMusicPlayer() {
        this.#requestMusicConfig();
        this.#requestMusicCategory();
    }

    initEventListener() {

    }

    #requestMusicConfig() {
        // TODO
    }

    #requestMusicCategory() {
        // 뮤직 카테고리, 뮤직, 뮤직 설정 정보 로드
        const xhr = new XMLHttpRequest();
        const blogId = !this.blogIdHiddenInput ? 0 : this.blogIdHiddenInput.value;

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
        xhr.open("GET", `/music/open/play-list?${urlSearchParam}`, true);

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
        const musicResponse = responseValue["musicPaginationResponse"];
        const musicMap = new Map();
        const musicCategoryMap = new Map();
        const musicConfigMap = new Map();
        //
        // const musicPlayerId = `${musicStoreMap["categoryId"]}&${musicStoreMap["musicId"]}`;
        //
        // 기본 값 지정
        musicConfigMap.set('config', {
            listFolded: true,
            listMaxHeight: 90,
            lrcType: 0,
            autoplay: true,
            mutex: true,
            order: 'random',
            mode: {
                fixed: true,
                mini: false
            }
        });

        musicResponse.forEach(music => {
            const musicPlayerId = `${music["musicId"]}&${music["categoryId"]}`;
            musicCategoryMap.set(musicPlayerId, {
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
        });

        musicMap.set('data', musicCategoryMap);
        musicMap.set('config', musicConfigMap.get('config'));

        this.musicUtilController.clearAudioPlayer();
        this.musicUtilController.initAudioPlayer(musicMap);

        if (musicConfigMap.get("config")["autoplay"] === true) {
            this.musicUtilController.autoPlayAudioPlayer();
        }
    }

    #handleMusicCategoryTemplate(responseValue) {
        const musicCategoryTemplate = document.getElementById("music-category-template").innerHTML;
        const musicCategoryTemplateObject = Handlebars.compile(musicCategoryTemplate);
        const jsonObj = responseValue["musicPaginationResponse"];
        const totalMusicTemplateHTML = `<option value="0">전체</option>`;
        const musicTemplateHTML = musicCategoryTemplateObject({"musicCategoryList": jsonObj});
        this.audioPlayerCategoryList.innerHTML = (totalMusicTemplateHTML + musicTemplateHTML);
    }
}