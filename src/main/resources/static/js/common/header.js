class HeaderController extends UtilController {
    constructor() {
        super();
        this.dayNightSkinButton = document.getElementById("day_night_skin_button");
        this.hostingButton = document.getElementById("hosting_button");
    }

    initHeaderController() {
        if (this.dayNightSkinButton != null) {
            this.dayNightSkinButton.addEventListener("click", evt => {
                const img = evt.target.closest("img");
                if (img.src.includes('sun')) {
                    img.src = '../image/night.png'
                } else if (img.src.includes('night')) {
                    img.src = '../image/sun.gif';
                }
            });
        }

        if (this.hostingButton != null) {
            this.hostingButton.addEventListener("click", evt => {
                this.showToastMessage("호스팅 정보는 추후에 공개 예정");
            });
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // 추후에 로드 시에, 서버에서 전달받은 데이터 로드 메서드 호출
    const headerController = new HeaderController();
    headerController.initHeaderController();
});