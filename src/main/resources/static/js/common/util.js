/**
 * 각종 기능 유틸리티 컨트롤러
 * **/
class UtilController {
    constructor() {
        this.initHandlerbars();
        this.idRegex = new RegExp('^[a-z]{1}[a-z0-9]{4,11}$');
        this.emailRegex = new RegExp('^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$');
        this.defaultUserProfileThumbnail = "../images/user_default_thumbnail.png";

        // post
        this.MAX_POST_CONTENT_SIZE = 10 * 1024 * 1024 // 압축사이즈: 10MB

        // intro
        this.MAX_INTRO_CONTENT_SIZE = 5 * 1024 * 1024; // 압축사이즈: 5MB

        this.MAX_IMAGE_UPLOAD_SIZE = 5 * 1024 * 1024; // 이미지 최대 업로드 사이즈: 5MB

        this.MAX_THUMBNAIL_IMAGE_UPLOAD_SIZE = 5 * 1024 * 1024; // 썸네일 이미지 최대 업로드 사이즈: 5MB

        this.RETRY_MAX_COUNT = 10; // API 요청 재시도 횟수

        this.UPLOAD_IMAGE_TYPE = "FILE_SERVER" // 이미지 업로드 방식: S3 | FILE_SERVER
    }

    initHandlerbars() {
        /**
         * 브라우저 타임존 기반 시간 변환 함수
         */
        Handlebars.registerHelper('convertTimeByBrowserTimezone', function (serverTime, pattern, isBaseTimezone) {
            if (!isBaseTimezone || isBaseTimezone === false) {
                return serverTime;
            }
            const offsetMinutes = new Date().getTimezoneOffset();
            const offsetHours = offsetMinutes / 60;

            // 시간대를 시와 분으로 분리합니다.
            const offsetHoursPart = Math.floor(Math.abs(offsetHours));
            const offsetMinutesPart = Math.abs(offsetMinutes % 60);

            // 시간대의 부호를 판단합니다.
            const offsetSign = offsetHours > 0 ? '-' : '+';

            // 양수 또는 음수인 시간대를 2자리 숫자로 포맷합니다.
            const formattedOffsetHours = offsetHoursPart.toString().padStart(2, '0');
            const formattedOffsetMinutes = offsetMinutesPart.toString().padStart(2, '0');
            const browserTimezoneOffset = `${offsetSign}${formattedOffsetHours}:${formattedOffsetMinutes}`;

            const convertedTime = moment.utc(serverTime).utcOffset(browserTimezoneOffset).format(pattern);
            return convertedTime;
        });

        Handlebars.registerHelper('ternaryOperator', function (object, target, valueIfTrue, valueIfFalse) {
            return (object === target) ? valueIfTrue : valueIfFalse;
        });

        Handlebars.registerHelper('ternarySelfOperator', function (object, target, valueIfTrue) {
            return (object === target) ? valueIfTrue : object;
        });

        Handlebars.registerHelper('nullCheck', param => {
            return param == null || param == '<<<undefined>>>';
        });

        Handlebars.registerHelper('hrefCheck', param => {
            return !param ? 'comment' : param;
        });

        Handlebars.registerHelper('existImage', image => {
            return image != null && image != '<<<undefined>>>';
        });

        Handlebars.registerHelper('getInnerText', tag => {
            if (tag == null) {
                return 'UNDEFINED';
            }
            return tag.replace(/(<([^>]+)>)/ig, "");
        });

        Handlebars.registerHelper('getUserProfileImage', image => {
            if (image === '<<<undefined>>>') {
                return '../images/comment_default_user_pic.png';
            } else {
                return image;
            }
        });

        Handlebars.registerHelper('getPostThumbnailImage', image => {
            if (image === '<<<undefined>>>') {
                return '../images/default_thumbnail.gif';
            } else {
                return image;
            }
        });

        Handlebars.registerHelper('getDefaultPostImage', image => {
            if (image === '<<<undefined>>>') {
                return '../images/default_post_image.png';
            } else {
                return image;
            }
        });

        Handlebars.registerHelper('getCheckedUserName', name => {
            if (!name || name === '<<<undefined>>>') {
                return '익명의유저';
            } else {
                return name;
            }
        });
    }

    /**
     * 브라우저 GMT(Greenwich Mean Time) 타임존 오프셋 - ex) -12:00 ~ 00:00 ~ +12:00
     */
    createBrowserTimezoneOffset() {
        const offsetMinutes = new Date().getTimezoneOffset();
        const offsetHours = offsetMinutes / 60;

        // 시간대를 시와 분으로 분리합니다.
        const offsetHoursPart = Math.floor(Math.abs(offsetHours));
        const offsetMinutesPart = Math.abs(offsetMinutes % 60);

        // 시간대의 부호를 판단합니다.
        const offsetSign = offsetHours > 0 ? '-' : '+';

        // 양수 또는 음수인 시간대를 2자리 숫자로 포맷합니다.
        const formattedOffsetHours = offsetHoursPart.toString().padStart(2, '0');
        const formattedOffsetMinutes = offsetMinutesPart.toString().padStart(2, '0');

        return `${offsetSign}${formattedOffsetHours}:${formattedOffsetMinutes}`;
    }

    /**
     * 브라우저 타임존 기반 시간 변환 함수
     * @param serverTime: 서버에서 반환 된 시간 (UTC,GMT)
     * @param pattern: 반환 시간 패턴 ex) 'YYYY-MM-DD HH:mm' , 'YYYY-MM-DD HH:mm:ss'
     */
    convertTimeByBrowserTimezone(serverTime, pattern, isBaseTimezone) {
        if (!isBaseTimezone || isBaseTimezone === false) {
            return serverTime;
        }
        const browserTimezoneOffset = this.createBrowserTimezoneOffset(); // 브라우저 타임존 오프셋
        const convertedTime = moment.utc(serverTime).utcOffset(browserTimezoneOffset).format(pattern);
        return convertedTime;
    }

    getBrowserLanguage() {
        return navigator.language || navigator.userLanguage;
    }

    checkPostContentSize(content, size) {
        return content > size;
    }

    getRemoveSpaceStr(str) {
        return str.replace(/\s/g, '');
    }

    getUrlStr() {
        return window.location.href;
    }

    getUrlStrAndParse(separator) {
        const url = this.getUrlStr();
        const parsed = url.split(separator);

        if (parsed.length > 1) {
            return parsed[1];
        } else {
            return null;
        }
    }

    scrollTargetElement(id) {
        if (!id) {
            return;
        }
        document.getElementById(id).scrollIntoView();
    }

    /** Loading Spinner **/
    loadingSpin(option, tagId) {
        const loadingTag = document.getElementById(tagId);

        if (loadingTag) {
            loadingTag.style.display = 'block';
            const spinner = new Spinner(option).spin(loadingTag);
            return spinner;
        } else {
            return null;
        }
    }

    /** Loading Spinner Stop **/
    loadingStop(spinner, tagId) {
        if (spinner) {
            spinner.stop();
            const loadingTag = document.getElementById(tagId);
            if (loadingTag) {
                loadingTag.style.display = 'none';
            }
        }
    }

    sleep(ms) {
        return new Promise((resolve) => setTimeout(resolve, ms))
    }

    showToastMessage(message, _duration) {
        Toastify({
            text: message,
            duration: _duration,
            close: true,
            position: "center",
            stopOnFocus: true,
            style: {
                background: "linear-gradient(to right, #00b09b, #96c93d)",
            }
        }).showToast();
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

    convertStrByteArr(obj, isEncode) {
        if (isEncode === true) {
            return obj.toString()
        } else {
            const parsed = obj.split(",");
            const uint8Arr = new Uint8Array(parsed.length);
            for (let i = 0; i < parsed.length; i++) {
                let elem = parseInt(parsed[i]);
                elem = (elem < 0) ? (elem * -1) : elem;
                uint8Arr[i] = elem;
            }
            return uint8Arr;
        }
    }

    /**
     * Compress & Decompress string content
     */
    compressContent(content, isCompress) {
        if (content == null) {
            return "";
        }

        if (isCompress === true) {
            const compressed = LZString.compressToBase64(content);
            return !compressed ? content : compressed;
        } else {
            const decompressed = LZString.decompressFromBase64(content);
            return (!decompressed || decompressed === 'P') ? content : decompressed;
        }
    }

    /**
     * HTML Content remove replace method
     */
    replaceAndSubHTMlTag(tag, limit) {
        const result = (tag == null) ? "" : tag.replace(/(<([^>]+)>)/ig, '');
        return limit <= 0 ? result : result.substring(0, limit);
    }

    /**
     * Quill Editor Utils
     * TODO: ImageBB(CDN) 도입 고려 (장점: 최대 64MB 이내 이미지 업로드 제한 X, 배치 관리 필요 X)
     * 링크: https://api.imgbb.com/
     * 이미지 여러개 첨부 시에, Java>SFTP 세션 및 연결 끊김 오류 발생 관련
     *
     * TODO: 링크 관련 개선 사항 진행 (ex: 네이버 블로그)
     * 링크 툴바 이벤트 핸들러 커스터마이징 + curl + 데이터 크롤링(Image, title, desc) + 템플릿 카드 정의 및 표시
     * **/
    getQuillHTML(htmlTag, data_gramm, contentEditable) {
        return (
            `<div class="ql-editor" data-gramm="${data_gramm}" contenteditable="${contentEditable}" data-placeholder="원하는 문장을 자유롭게 입력하세요. :)">` +
            htmlTag +
            `</div>`
        );
    }

    getReadOnlyQuillEditor(id) {
        Quill.register("modules/imageCompressor", imageCompressor);

        return new Quill(`#${id}`, {
            modules: {
                syntax: true,
                toolbar: false
            },
            theme: 'snow',
            readOnly: true
        });
    }

    getQuillEditor(id) {
        Quill.register("modules/imageCompressor", imageCompressor);
        const quill = new Quill(`#${id}`, {
            modules: {
                "emoji-toolbar": true,
                "emoji-shortname": true,
                "emoji-textarea": true,
                imageDrop: false,
                imageResize: {
                    modules: ['Resize', 'DisplaySize', 'Toolbar']
                },
                syntax: true,
                // imageCompressor: {
                //     quality: 1,
                //     maxWidth: 1000, // default
                //     maxHeight: 1000, // default
                //     ignoreImageTypes: ['image/gif']
                // },
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
                    ['emoji'],
                    ['codeBlock'],
                    ['trash']
                ]
            },
            'image-tooltip': true,
            'link-tooltip': true,
            theme: 'snow',
            placeholder: '원하는 문장을 자유롭게 입력하세요. :)'
        });

        quill.getModule('toolbar').addHandler('image', () => {
            const input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', 'image/*');
            input.click();

            input.addEventListener("change", async () => {
                const imgFile = input.files[0];
                try {
                    if (this.checkImageFileExtension(imgFile, ['jpg', 'jpeg', 'png', 'gif', 'GIF'])) {
                        if (this.checkImageFileExtension(imgFile, ['gif', 'GIF']) && this.checkImageFileBySize(imgFile, this.MAX_IMAGE_UPLOAD_SIZE)) {
                            // if file extension is gif | GIF, 5MB가 넘지 않는 경우, 압축 진행 X
                            const fileReader = new FileReader();

                            fileReader.onload = (event) => {
                                const formData = new FormData();
                                const xhr = new XMLHttpRequest();
                                const uploadKeyDocument = document.getElementById("upload_key")
                                let uploadKey = uploadKeyDocument.value;

                                if (!uploadKey || uploadKey === '' || uploadKey === 'undefined') {
                                    uploadKey = new Date().getTime();
                                    uploadKeyDocument.value = uploadKey;
                                }

                                xhr.open("POST", `/post/upload/post-image/${uploadKey}`, true);
                                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

                                xhr.addEventListener("loadend", event => {
                                    let status = event.target.status;
                                    const responseValue = event.target.responseText;

                                    if ((status >= 400 && status <= 500) || (status > 500)) {
                                        this.showToastMessage(responseValue);
                                    } else {
                                        quill.editor.insertEmbed(quill.getSelection().index, 'image', responseValue);
                                    }
                                });

                                xhr.addEventListener("error", event => {
                                    this.showToastMessage('오류가 발생하여 이미지 전송에 실패하였습니다.');
                                });

                                formData.append("compressed_post_image", imgFile);
                                xhr.send(formData);
                            }
                            fileReader.readAsDataURL(imgFile);
                        } else {
                            this.getCompressedImageFile(imgFile).then(compressedImgFile => {
                                const fileReader = new FileReader();

                                fileReader.onload = (event) => {
                                    const formData = new FormData();
                                    const xhr = new XMLHttpRequest();
                                    const uploadKeyDocument = document.getElementById("upload_key")
                                    let uploadKey = uploadKeyDocument.value;

                                    if (!uploadKey || uploadKey === '' || uploadKey === 'undefined') {
                                        uploadKey = new Date().getTime();
                                        uploadKeyDocument.value = uploadKey;
                                    }

                                    xhr.open("POST", `/post/upload/post-image/${uploadKey}`, true);
                                    xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

                                    xhr.addEventListener("loadend", event => {
                                        let status = event.target.status;
                                        const responseValue = event.target.responseText;

                                        if ((status >= 400 && status <= 500) || (status > 500)) {
                                            this.showToastMessage(responseValue);
                                        } else {
                                            quill.editor.insertEmbed(quill.getSelection().index, 'image', responseValue);
                                            quill.editor.insertEmbed(quill.getSelection().index + 1, 'block', '<p><br></p>');
                                        }
                                    });

                                    xhr.addEventListener("error", event => {
                                        this.showToastMessage('오류가 발생하여 이미지 전송에 실패하였습니다.');
                                    });

                                    formData.append("compressed_post_image", compressedImgFile);
                                    xhr.send(formData);
                                }
                                fileReader.readAsDataURL(compressedImgFile);
                            });
                        }
                    } else {
                        this.showToastMessage("지정 된 이미지 파일 ('jpg', 'jpeg', 'png', 'gif', 'GIF')만 업로드 가능합니다.");
                    }
                } catch (error) {
                    this.showToastMessage("ERROR: " + error);
                }
            })
        });

        /**
         * Batch Job 통해 주기(N년) 동안 참조되지 않은 리소스 삭제하도록 (방법 조사)
         * Chat Gpt 답변 참조 ↓↓↓
         *
         * Linux에서 파일의 마지막 참조 날짜(Atime)를 확인하는 방법은 stat 명령어를 사용하는 것입니다. stat 명령어는 파일의 상세 정보를 보여주는데, 여기서 마지막 참조 날짜를 확인할 수 있습니다.
         * 터미널을 열고 다음 명령어를 입력하여 파일의 마지막 참조 날짜를 확인할 수 있습니다:
         *
         * stat -c %x <파일 경로>
         * <파일 경로>에는 확인하고자 하는 파일의 경로를 입력하시면 됩니다. 위 명령어를 실행하면 파일의 마지막 참조 날짜가 출력됩니다.
         *
         * 만약 날짜와 시간까지 자세하게 보고 싶다면 아래와 같이 명령어를 사용할 수 있습니다:
         *
         * stat -c %x\ %X <파일 경로>
         * 여기서 %x는 날짜를, %X는 시간을 나타내는 포맷 지시자입니다.
         *
         * 또한, ls 명령어를 사용하여 파일 목록을 보고 마지막 참조 날짜를 함께 표시할 수도 있습니다:
         *
         * ls -l --time=atime <파일 경로>
         * 위의 명령어를 실행하면 파일의 상세 정보와 마지막 참조 날짜가 함께 표시됩니다.
         */
        // quill.on('text-change', (delta, oldContents, source) => {
        //     if (source !== 'user') return;
        //     const deletedImgList = this.getQuillEditorImgUrls(quill.getContents().diff(oldContents));
        //
        //     if (deletedImgList && deletedImgList.length > 0) {
        //         const formData = new FormData();
        //         const xhr = new XMLHttpRequest();
        //
        //         xhr.open("POST", `/post/delete/post-image`, true);
        //         xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        //
        //         xhr.addEventListener("loadend", event => {
        //             let status = event.target.status;
        //             const responseValue = event.target.responseText;
        //
        //             if ((status >= 400 && status <= 500) || (status > 500)) {
        //                 this.showToastMessage(responseValue);
        //             }
        //         });
        //
        //         xhr.addEventListener("error", event => {
        //             this.showToastMessage('오류가 발생하여 이미지 삭제에 실패하였습니다.');
        //         });
        //
        //         formData.append("imgSrcList", deletedImgList);
        //         xhr.send(formData);
        //     }
        // });

        /**
         * TODO custom button implement
         * link: https://github.com/T-vK/DynamicQuillTools
         * 게시글 크기 조절 사이드바
         * 단어 코드 표시(노션 참고)
         * 기타 등등 필요 시에 커스텀 추가
         * */
        const codeBlockButton = document.querySelector('.ql-codeBlock');
        codeBlockButton.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" height="16" width="18" viewBox="0 0 576 512">
        <path d="M315 315l158.4-215L444.1 70.6 229 229 315 315zm-187 5l0 0V248.3c0-15.3 7.2-29.6 19.5-38.6L420.6 8.4C428 2.9 437 0 446.2 0c11.4 0 22.4 4.5 30.5 12.6l54.8 54.8c8.1 8.1 12.6 19 12.6 30.5c0 9.2-2.9 18.2-8.4 25.6L334.4 396.5c-9 12.3-23.4 19.5-38.6 19.5H224l-25.4 25.4c-12.5 12.5-32.8 12.5-45.3 0l-50.7-50.7c-12.5-12.5-12.5-32.8 0-45.3L128 320zM7 466.3l63-63 70.6 70.6-31 31c-4.5 4.5-10.6 7-17 7H24c-13.3 0-24-10.7-24-24v-4.7c0-6.4 2.5-12.5 7-17z"/>
    </svg>`;
        codeBlockButton.addEventListener('click', function (evt) {
            // Get the current selection
            const selection = quill.getSelection();

            if (selection && selection.length > 0) {
                const selectionIndex = selection.index;
                const selectionLength = selection.length;
                quill.insertText(selection.index + selectionLength, ' ');
                // Apply the desired style to the selected text
                quill.formatText(selection, 'code', {
                    'background-color': 'rgb(249, 242, 244)',
                    'color': 'rgb(199, 37, 78)',
                });
            }
        });

        const trashButton = document.querySelector('.ql-trash');
        trashButton.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16"> <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/> <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/> </svg>`;
        trashButton.addEventListener('click', evt => {
            const selection = quill.getSelection();
            if (selection) {
                // Check if there is a range selected
                if (selection.length > 0) {
                    quill.deleteText(selection.index, selection.length);
                } else {
                    // No text is selected, but check if there's an image selected
                    const [blot] = quill.scroll.descendant(Image, selection.index);
                    if (blot) {
                        quill.deleteText(selection.index, blot.length());
                    }
                }
            }
        });
        return quill;
    }

    getQuillEditorImgUrls(delta) {
        return delta.ops.filter(i => i.insert && i.insert.image).map(i => i.insert.image);
    }

    initCalendar() {
        const calendarEl = document.getElementById('calendar');
        const calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth', // 초기 로드 될때 보이는 캘린더 화면(기본 설정: 달)
            titleFormat: function (date) {
                const year = date.date.year;
                const month = (parseInt(date.date.month) + 1);
                return `${year}.${month}`;
            },
            //initialDate: '2021-07-15', // 초기 날짜 설정 (설정하지 않으면 오늘 날짜가 보인다.)
            selectable: true, // 달력 일자 드래그 설정가능
            droppable: true,
            editable: true,
            scrollTime: '00:00:00',
            nowIndicator: true, // 현재 시간 마크
            locale: this.getBrowserLanguage() // 한국어 설정
        });
        calendar.render();
    }

    getDefaultUserProfileThumbnail() {
        return this.defaultUserProfileThumbnail;
    }

    async compressImageFiles(imgFile) {
        return await imageCompression(
            imgFile,
            // you should provide one of maxSizeMB, maxWidthOrHeight in the options
            {
                maxSizeMB: this.MAX_IMAGE_UPLOAD_SIZE,          // 5MB
                onProgress: Function,       // optional, a function takes one progress argument (percentage from 0 to 100)
                useWebWorker: true,      // optional, use multi-thread web worker, fallback to run in main-thread (default: true)
            });
    }

    async getCompressedImageFile(imgFile) {
        return await this.compressImageFiles(imgFile).then(compressedImgFile => {
            return compressedImgFile;
        });
    }

    checkImageFile(imgFile) {
        if (imgFile) {
            if (!this.checkImageFileExtension(imgFile, ['jpg', 'jpeg', 'png', 'gif', 'GIF'])) {
                this.showToastMessage("지정 된 이미지 파일 ('jpg', 'jpeg', 'png', 'gif', 'GIF')만 업로드 가능합니다.");
                return false;
            } else if (!this.checkImageFileBySize(imgFile, this.MAX_IMAGE_UPLOAD_SIZE)) {
                this.showToastMessage('최대 업로드 파일 크기는 5MB 입니다.');
                return false;
            }
        }
        return true;
    }

    checkImageFile(imgFile, size) {
        if (imgFile) {
            if (!this.checkImageFileExtension(imgFile, ['jpg', 'jpeg', 'png', 'gif', 'GIF'])) {
                this.showToastMessage("지정 된 이미지 파일 ('jpg', 'jpeg', 'png', 'gif', 'GIF')만 업로드 가능합니다.");
                return false;
            } else if (!this.checkImageFileBySize(imgFile, size * 1024 * 1024)) {
                this.showToastMessage('최대 업로드 파일 크기는 ' + size + 'MB 입니다.');
                return false;
            }
        }
        return true;
    }

    checkImageFileExtension(imgFile, fileForms) {
        if (imgFile) {
            const fileExtension = imgFile.name.slice(imgFile.name.lastIndexOf(".") + 1);

            if (!fileForms.includes(fileExtension)) {
                return false;
            } else {
                return true;
            }
        }
    }

    checkImageFileBySize(imgFile, fileSize) {
        if (imgFile) {
            if (imgFile.size >= fileSize) {
                return false;
            }
        }
        return true;
    }

    getQueryParam(page, recordSize, pageSize) {
        return new URLSearchParams({
            page: (page) ? page : 1,
            recordSize: recordSize,
            pageSize: pageSize
            // TODO 필요에 따라 keyword, searchType 등등 추가로 정의
        });
    }

    drawBasicPagination(pagination, queryParam, url) {
        let html = '';

        // 첫 페이지, 이전 페이지
        if (pagination["existPrevPage"]) {
            html += `
              <!--
               <li class="page-item">
                    <button class="page-link" aria-label="Previous" url="${url}" page="1">
                    <span aria-hidden="true">&laquo;</span>
                    </button>
                </li>
              -->
                
                <li class="page-item">
                    <button class="page-link" aria-label="Previous" url="${url}" page="${pagination["startPage"] - 1}">
                    <span aria-hidden="true">&laquo;</span>
                    </button>
                </li>
            `;
        }

        // 페이지 번호
        for (let i = pagination["startPage"]; i <= pagination["endPage"]; i++) {
            if (i <= 0)
                continue;

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
                    <span aria-hidden="true">&raquo;</span>
                    </button>
                </li>
                
                <!--
                <li class="page-item">
                    <button class="page-link" aria-label="Previous" url="${url}" page="${pagination["totalPageCount"]}">
                    <span aria-hidden="true">&lsaquo;</span>
                    </button>
                </li>
                -->
            `;
        }
        return html;
    }

    drawSimplePagination(pagination, queryParam, url) {
        let html = '';

        // 첫 페이지, 이전 페이지
        if (pagination["existPrevPage"]) {
            html += `
               <!--
               <li class="page-item">
                    <button class="page-link simple_page_button_style" aria-label="Previous" url="${url}" page="1">
                    <span aria-hidden="true">&laquo;</span>
                    </button>
                </li>
                -->
                
                <li class="page-item">
                    <button class="page-link simple_page_button_style" aria-label="Previous" url="${url}" page="${pagination["startPage"] - 1}">
                    <span aria-hidden="true">&laquo;</span>
                    </button>
                </li>
            `;
        }

        // 페이지 번호
        for (let i = pagination["startPage"]; i <= pagination["endPage"]; i++) {
            if (i <= 0)
                continue;

            const active = (i === parseInt(queryParam.get("page"))) ? 'active' : '';
            html += `
             <li class="page-item ${active}">
                    <button class="page-link simple_page_button_style" url="${url}" page="${i}">${i}</button>
                </li>
            `;
        }

        // 다음 페이지, 마지막 페이지
        if (pagination["existNextPage"]) {
            html += `
             <li class="page-item">
                    <button class="page-link simple_page_button_style" aria-label="Previous" url="${url}" page="${pagination["endPage"] + 1}">
                    <span aria-hidden="true">&raquo;</span>
                    </button>
                </li>
                
                <!--
                <li class="page-item">
                    <button class="page-link simple_page_button_style" aria-label="Previous" url="${url}" page="${pagination["totalPageCount"]}">
                    <span aria-hidden="true">&lsaquo;</span>
                    </button>
                </li>
                -->
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

    invokeAutoSaveInterval(func, contents, time) {
        return setInterval(func, time);
    }

    clearInterval(interval, intervalKey) {
        clearInterval(interval);
        localStorage.removeItem(intervalKey);
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