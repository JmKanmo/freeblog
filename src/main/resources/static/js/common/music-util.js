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
                this.musicPlayer.set("NONE", new APlayer({
                    container: document.getElementById(this.documentId),
                    listFolded: musicConfig["listFolded"],
                    listMaxHeight: musicConfig["listMaxHeight"],
                    lrcType: musicConfig["lrcType"],
                    autoplay: musicConfig["autoplay"],
                    mutex: musicConfig["mutex"],
                    order: musicConfig["order"],
                    mini: musicConfig["mini"],
                    fixed: musicConfig["fixed"],
                    audio: []
                }));
            } else {
                musicData.forEach((value, key) => {
                    const category = key;
                    const audios = value["audio"];

                    this.musicPlayer.set(category, new APlayer({
                        container: document.getElementById(this.documentId),
                        listFolded: musicConfig["listFolded"],
                        listMaxHeight: musicConfig["listMaxHeight"],
                        lrcType: musicConfig["lrcType"],
                        autoplay: musicConfig["autoplay"],
                        mutex: musicConfig["mutex"],
                        order: musicConfig["order"],
                        mini: musicConfig["mini"],
                        fixed: musicConfig["fixed"],
                        audio: audios
                    }));
                });
            }
        }
        return null;
    }

    setAudioPlayer(musicData) {
        const musicMap = new Map();
        const musicCategoryMap = new Map();
        const musicConfigMap = new Map();

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

    playAudioPlayer(id) {
        const ap = this.musicPlayer.get(id);

        if (ap) {
            ap.play();
        }
    }
}