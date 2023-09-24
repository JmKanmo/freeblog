class MusicPlayController extends UtilController {
    constructor() {
        super();
        this.musicStoreListBox = document.getElementById("musicStoreListBox");
        this.musicStorePagination = document.getElementById("musicStorePagination");
        // page size
        this.musicStoreRecordSize = 5;
        this.musicStorePageSize = 5;
    }

    initMusicPlayController() {
        this.#requestMusicStoreList();
        this.initEventListener();
    }

    #requestMusicStoreList(url) {
        // TODO
    }

    initEventListener() {

    }
}

document.addEventListener("DOMContentLoaded", () => {
    const musicPlayController = new MusicPlayController();
    musicPlayController.initMusicPlayController();
});