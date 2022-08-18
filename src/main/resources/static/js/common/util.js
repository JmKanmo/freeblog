/**
 * 각종 기능 유틸리티 컨트롤러
 * **/
class UtilController {
    constructor() {
        this.initHandlerbars();
        this.idRegex = new RegExp('^[a-z]{1}[a-z0-9]{4,11}$');
        this.emailRegex = new RegExp('^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$');
        this.defaultUserProfileThumbnail = "../images/user_default_thumbnail.png";
    }

    initHandlerbars() {
        Handlebars.registerHelper('isDiscountZero', discount => {
            return discount <= 0 ? true : false;
        });
    }

    /** 오픈소스 참조 (로딩 중 화면 만들기) **/
    loadingWithMask(width, height) {
        //화면의 높이와 너비를 구합니다.
        const maskHeight = height;
        const maskWidth = width;

        //화면에 출력할 마스크를 설정해줍니다.
        const mask = `<div id='mask' style='position:absolute; z-index:9000; background-color:#000000; display:none; left:0; top:0;'></div>`;
        let loadingImg = ``;

        loadingImg += "<div id='loadingImg' style='position:absolute; top: calc(50% - (200px / 2)); width:100%; z-index:99999999;'>";
        loadingImg += `<img src='../images/loading_img.gif' style='position: relative; display: block; margin: 0px auto;'/>`;
        loadingImg += `</div>`;

        //화면에 레이어 추가
        $('body')
            .append(mask)
            .append(loadingImg)

        //마스크의 높이와 너비를 화면 것으로 만들어 전체 화면을 채웁니다.
        $('#mask').css({
            'width': maskWidth
            , 'height': maskHeight
            , 'opacity': '0.3'
        });

        //마스크 표시
        $('#mask').show();

        //로딩중 이미지 표시
        $('#loadingImg').show();
    }

    /** 오픈소스 참조 (로딩 중 화면 닫기) **/
    closeLoadingWithMask() {
        $('#mask, #loadingImg').hide();
        $('#mask, #loadingImg').empty();
    }

    showToastMessage(message) {
        Toastify({
            text: message,
            duration: 3000,
            close: true,
            position: "center",
            stopOnFocus: true,
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
            }
        }).showToast();
    }

    checkEmailRegExp(email) {
        return this.emailRegex.test(email);
    }

    checkIdRegExp(id) {
        return this.idRegex.test(id);
    }

    openPopUp(width, height, url, target) {
        let left = (document.body.offsetWidth / 2) - (width / 2);
        let tops = (document.body.offsetHeight / 2) - (height / 2);
        left += window.screenLeft;
        return window.open(url, target, `width=${width}, height=${height}, left=${left}, top=${tops}`);
    }

    getQuillEditor(id) {
        Quill.register("modules/imageCompressor", imageCompressor);

        return new Quill(`#${id}`, {
            modules: {
                "emoji-toolbar": true,
                "emoji-shortname": true,
                "emoji-textarea": true,
                imageDrop: true,
                imageResize: {
                    modules: ['Resize', 'DisplaySize', 'Toolbar']
                },
                syntax: true,
                imageCompressor: {
                    quality: 0.9,
                    maxWidth: 1000, // default
                    maxHeight: 1000, // default
                    imageType: 'image/png, image/gif, image/jpeg, image/jpg, image/GIF'
                },
                toolbar: [
                    ['bold', 'italic', 'underline', 'strike'],        // toggled buttons
                    ['blockquote', 'code-block'],
                    [{'header': 1}, {'header': 2}],               // custom button values
                    [{'list': 'ordered'}, {'list': 'bullet'}],
                    [{'script': 'sub'}, {'script': 'super'}],      // superscript/subscript
                    [{'indent': '-1'}, {'indent': '+1'}],          // outdent/indent
                    [{'direction': 'rtl'}],                         // text direction
                    ['link', 'image', 'video', 'formula'],          // add image support
                    [{'size': ['small', false, 'large', 'huge']}],  // custom dropdown
                    [{'header': [1, 2, 3, 4, 5, 6, false]}],
                    [{'color': []}, {'background': []}],          // dropdown with defaults from theme
                    [{'font': []}],
                    [{'align': []}],
                    ['clean'],                                        // remove formatting button
                    ['emoji']
                ]
            },
            'image-tooltip': true,
            'link-tooltip': true,
            theme: 'snow',
            placeholder: '소개를 입력해주세요.'
        });
    }

    initAudioPlayer() {
        const ap = new APlayer({
            container: document.getElementById('audio_player'),
            listFolded: true,
            listMaxHeight: 90,
            lrcType: 3,
            audio: [
                {
                    name: '솜사탕',
                    artist: '풍댕이',
                    url: 'http://54.235.18.40/audio/jmblog/sweetcandy.mp3',
                    cover: 'http://54.235.18.40/images/jmblog/가르강튀아.jpg',
                    lrc: '가사 없음',
                    theme: '청순섹시'
                },
                {
                    name: '익명의 노래',
                    artist: '익명의 아티스트',
                    url: 'http://54.235.18.40/audio/jmblog/backsound.mp3',
                    cover: '../images/default_thumbnail.gif',
                    lrc: '가사 없음',
                    theme: '익명의 테마'
                }
            ]
        });
        return ap;
    }

    initCalendar() {
        const calendarEl = document.getElementById('calendar');
        const calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth', // 초기 로드 될때 보이는 캘린더 화면(기본 설정: 달)
            titleFormat: function (date) {
                return date.date.year + '년 ' + (parseInt(date.date.month) + 1) + '월';
            },
            //initialDate: '2021-07-15', // 초기 날짜 설정 (설정하지 않으면 오늘 날짜가 보인다.)
            selectable: true, // 달력 일자 드래그 설정가능
            droppable: true,
            editable: true,
            nowIndicator: true, // 현재 시간 마크
            locale: 'ko' // 한국어 설정
        });
        calendar.render();
    }

    getDefaultUserProfileThumbnail() {
        return this.defaultUserProfileThumbnail;
    }

    checkProfileThumbnailFile(input) {
        let imgFile = input.files;

        if (imgFile && imgFile[0]) {
            let fileForms = ['jpg', 'jpeg', 'png', 'gif', 'GIF'];
            let fileSize = 20 * 1024 * 1024; // 20MB
            let fileExtension = input.value.slice(input.value.lastIndexOf(".") + 1)

            if (!fileForms.includes(fileExtension)) {
                this.showToastMessage("지정 된 이미지 파일만 업로드 가능합니다.");
                return false;
            } else if (imgFile[0].size >= fileSize) {
                this.showToastMessage('최대 파일 사이즈는 20MB 입니다.');
                return false;
            }
        }
        return true;
    }
}

/**
 * 로그인 팝업 컨트롤러
 */
class LoginPopUpController extends UtilController {
    constructor() {
        super();

        // pop up
        this.formPopUpWindow = document.getElementById("formPopUp");
        this.loginButton = document.getElementById("login_button");
        this.popUpCloseButton = document.getElementById("closePopUpButton");
    }

    initLoginPopUpController() {
        // login button event listener
        if (this.loginButton != null) {
            this.loginButton.addEventListener("click", evt => {
                if (this.formPopUpWindow.style.display === '' || this.formPopUpWindow.style.display === 'none') {
                    this.formPopUpWindow.style.display = 'block';
                }
            });
        }

        // login pop up event listener
        if (this.popUpCloseButton != null) {
            this.popUpCloseButton.addEventListener("click", evt => {
                this.formPopUpWindow.style.display = 'none';
            });
        }
    }
}