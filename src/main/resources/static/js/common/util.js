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
        Handlebars.registerHelper('existImage', image => {
            return image != null && image != '<<<undefined>>>';
        });

        Handlebars.registerHelper('getInnerText', tag => {
            return tag.replace(/(<([^>]+)>)/ig, "");
        });
    }

    /**
     * 오픈소스 참조 (로딩 중 화면 만들기)
     * 추후 다른 라이브러리 대체
     * **/
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

    showToastMessage(message, isClose, duration, dismissListener) {
        Toastify({
            text: message,
            duration: duration,
            close: isClose,
            position: "center",
            stopOnFocus: true,
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
            },
            callback: dismissListener
        }).showToast();
    }

    checkSpecialCharacter(text) {
        const regExp = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]/g;

        if (regExp.test(text)) {
            return true;
        } else {
            return false;
        }
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
            placeholder: '원하는 문장을 자유롭게 입력하세요. :)'
        });
    }

    initAudioPlayer() {
        const ap = new APlayer({
            container: document.getElementById('audio_player'),
            listFolded: true,
            listMaxHeight: 90,
            lrcType: 0,
            audio: [
                {
                    name: '솜사탕',
                    artist: '풍댕이',
                    url: 'https://freelog-s3-bucket.s3.amazonaws.com/audio/%ED%92%8D%EB%8C%95%EC%9D%B4+-+cotton+candy.mp3',
                    cover: 'https://freelog-s3-bucket.s3.amazonaws.com/image/2022-08-23T08%3A59%3A46.89279060054c6e247-cac0-37da-af49-8a1950e4f618.jpg',
                    theme: '청순섹시'
                },
                {
                    name: 'strawberry moon',
                    artist: 'IU',
                    url: 'https://freelog-s3-bucket.s3.amazonaws.com/audio/%EC%95%84%EC%9D%B4%EC%9C%A0+-+strawberry+moon.mp3',
                    cover: 'https://freelog-s3-bucket.s3.amazonaws.com/image/iu_strawberrymoon.jpg',
                    theme: '달콤'
                },
                {
                    name: '불가살 - 하루',
                    artist: '포맨',
                    url: 'https://freelog-s3-bucket.s3.amazonaws.com/audio/4MEN+(%ED%8F%AC%EB%A7%A8)+-+%ED%95%98%EB%A3%A8+(Bulgasal_+Immortal+Souls+%EB%B6%88%EA%B0%80%EC%82%B4+OST+Part+1).mp3',
                    cover: 'https://freelog-s3-bucket.s3.amazonaws.com/image/%EB%B6%88%EA%B0%80%EC%82%B4_%EC%9D%B4%EB%AF%B8%EC%A7%80.jpg',
                    theme: '불가살 테마'
                },
                {
                    name: '너,너',
                    artist: '스트레이',
                    url: 'https://freelog-s3-bucket.s3.amazonaws.com/audio/%EC%8A%A4%ED%8A%B8%EB%A0%88%EC%9D%B4++%EB%84%88+%EB%84%88.mp3',
                    cover: 'https://freelog-s3-bucket.s3.amazonaws.com/image/%EC%8A%A4%ED%8A%B8%EB%A0%88%EC%9D%B4+%EB%84%88+%EB%84%88.jpg',
                    theme: '불가살 테마'
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

    getQueryParam(page) {
        return new URLSearchParams({
            page: (page) ? page : 1,
            recordSize: 7,
            pageSize: 7
            // TODO 필요에 따라 keyword, searchType 등등 추가로 정의
        });
    }

    drawPagination(pagination, queryParam, url) {
        let html = '';

        // 첫 페이지, 이전 페이지
        if (pagination["existPrevPage"]) {
            html += `
               <li class="page-item">
                    <button class="page-link" aria-label="Previous" url="${url}" page="1">
                    <span aria-hidden="true">&laquo;</span>
                    </button>
                </li>
                
                <li class="page-item">
                    <button class="page-link" aria-label="Previous" url="${url}" page="${pagination["startPage"] - 1}">
                    <span aria-hidden="true">&lsaquo;</span>
                    </button>
                </li>
            `;
        }

        // 페이지 번호
        for (let i = pagination["startPage"]; i <= pagination["endPage"]; i++) {
            const active = (i === parseInt(queryParam.get("page"))) ? 'active' : '';
            html += `
             <li class="page-item ${active}">
                    <button class="page-link" url="${url}" page="${i}">${i}</button>
                </li>
            `;
        }

        // 다음 페이지, 마지막 페이지
        if (pagination["existNextPage"]) {
            html += `
             <li class="page-item">
                    <button class="page-link" aria-label="Previous" url="${url}" page="${pagination["endPage"] + 1}">
                    <span aria-hidden="true">&laquo;</span>
                    </button>
                </li>
                
                <li class="page-item">
                    <button class="page-link" aria-label="Previous" url="${url}" page="${pagination["totalPageCount"]}">
                    <span aria-hidden="true">&lsaquo;</span>
                    </button>
                </li>
            `;
        }
        return html;
    }

    copyUrl() {
        const url = window.document.location.href;
        const textarea = document.createElement("textarea");
        document.body.appendChild(textarea);
        textarea.value = url;
        textarea.select();
        document.execCommand("copy");
        document.body.removeChild(textarea);
        alert("URL이 복사되었습니다.")
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