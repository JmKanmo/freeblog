class MusicUtilController {
    constructor() {
        this.musicPlayer = new Map();
        this.documentId = `audio_player`;
        this.TOTAL_MUSIC_CATEGORY_INDEX = 1; // 전체 카테고리 디폴트 인덱스는 1로 규정
    }

    initAudioPlayer(musicMap, find) {
        const musicConfig = musicMap.get("config");
        const musicData = musicMap.get("data");

        if (musicMap && musicConfig) {
            if (musicData.size <= 0) {
                if (musicConfig["mode"]["mini"] === true) {
                    this.musicPlayer.set("NONE", new APlayer({
                        container: document.getElementById(this.documentId),
                        listFolded: musicConfig["listFolded"],
                        listMaxHeight: musicConfig["listMaxHeight"] + 'px',
                        lrcType: musicConfig["lrcType"],
                        autoplay: musicConfig["autoplay"],
                        mutex: musicConfig["mutex"],
                        order: musicConfig["order"],
                        loop: musicConfig["loop"],
                        mini: musicConfig["mode"]["mini"],
                        audio: []
                    }));
                } else if (musicConfig["mode"]["fixed"] === true) {
                    this.musicPlayer.set("NONE", new APlayer({
                        container: document.getElementById(this.documentId),
                        listFolded: musicConfig["listFolded"],
                        listMaxHeight: musicConfig["listMaxHeight"] + 'px',
                        lrcType: musicConfig["lrcType"],
                        autoplay: musicConfig["autoplay"],
                        mutex: musicConfig["mutex"],
                        order: musicConfig["order"],
                        loop: musicConfig["loop"],
                        fixed: null, // fixed:true로 햇더니, UI가 바닥에 고꾸라짐...
                        audio: []
                    }));
                }
            } else {
                musicData.forEach((value, key) => {
                    const category = key;
                    const audios = value["audio"];

                    if (musicConfig["mode"]["mini"] === true) {
                        this.musicPlayer.set(category, new APlayer({
                            container: document.getElementById(this.documentId),
                            listFolded: musicConfig["listFolded"],
                            listMaxHeight: musicConfig["listMaxHeight"] + 'px',
                            lrcType: musicConfig["lrcType"],
                            autoplay: musicConfig["autoplay"],
                            mutex: musicConfig["mutex"],
                            order: musicConfig["order"],
                            mini: musicConfig["mode"]["mini"],
                            loop: musicConfig["loop"],
                            audio: audios
                        }));
                    } else if (musicConfig["mode"]["fixed"] === true) {
                        this.musicPlayer.set(category, new APlayer({
                            container: document.getElementById(this.documentId),
                            listFolded: musicConfig["listFolded"],
                            listMaxHeight: musicConfig["listMaxHeight"] + 'px',
                            lrcType: musicConfig["lrcType"],
                            autoplay: musicConfig["autoplay"],
                            mutex: musicConfig["mutex"],
                            order: musicConfig["order"],
                            fixed: null, // fixed:true로 햇더니, UI가 바닥에 고꾸라짐...
                            loop: musicConfig["loop"],
                            audio: audios
                        }));
                    }
                });
            }
        }
        return null;
    }

    setAudioPlayer(musicData) {
        const musicMap = new Map();
        const musicCategoryMap = new Map();
        const musicConfigMap = new Map();

        // 기본 값 지정, 추후에 DB 저장 및 페이지 구성을 통해 관리
        musicConfigMap.set('config', {
            listFolded: true,
            listMaxHeight: 90,
            lrcType: 0,
            autoplay: false,
            mutex: true,
            order: 'list', // list | random
            loop: 'one', // one | all | none
            mode: {
                fixed: true,
                mini: false
            }
        });

        if (musicData) {
            const responseValue = JSON.parse(musicData);

            for (let idx in responseValue) {
                const musicData = responseValue[idx];
                const categoryId = musicData["categoryId"];
                const mapValue = musicCategoryMap.get(categoryId);

                if (mapValue) {
                    const audioList = mapValue["audio"];
                    audioList.push({
                        name: musicData["name"],
                        artist: musicData["artist"],
                        url: musicData["url"],
                        cover: musicData["cover"],
                        theme: musicData["categoryName"],
                    });
                } else {
                    musicCategoryMap.set(categoryId, {
                        name: musicData["categoryName"],
                        audio: [
                            {
                                name: musicData["name"],
                                artist: musicData["artist"],
                                url: musicData["url"],
                                cover: musicData["cover"],
                                theme: musicData["categoryName"],
                            }
                        ]
                    });
                }
                idx++;
            }
        }
        musicMap.set('data', musicCategoryMap);
        musicMap.set('config', musicConfigMap.get('config'));
        this.initAudioPlayer(musicMap);
    }

    clearAudioPlayer() {
        this.musicPlayer.clear();
    }

    /*
        아래 버그 해결 전까지 개발 진행 X
        특이사항: 리스트 내 원소가 1개 일땐 정상 동작, 여러개 경우, 동작 X
        ap.play() bug report
        https://github.com/DIYgod/APlayer/issues/784
     */
    autoPlayAudioPlayer(category, idx) {
        const musicPlayInfo = localStorage.getItem("musicPlayInfo");

        if (musicPlayInfo) {
            // TODO
        } else {
            //
            const ap = this.musicPlayer.get(category);
            if (ap) {
                ap.play();
            }
        }
    }

    playAudioPlayer(id) {
        const ap = this.musicPlayer.get(id);

        if (ap) {
            ap.play();
        }
    }
}