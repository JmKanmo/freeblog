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

        // video tus upload config
        this.videoFileInput = document.getElementById("video_file_input");
        this.MAX_UPLOAD_VIDEO_FILE_SIZE = 300 * 1024 * 1024; // 동영상 최대 업로드 사이즈: 300MB

        // image option
        this.imageWidthInput = document.getElementById("imageWidthInput");
        this.imageHeightInput = document.getElementById("imageHeightInput");
        this.imageOptionSettingButton = document.getElementById("imageOptionSettingButton");
        this.imageDeleteOptionButton = document.getElementById("imageDeleteOptionButton");
        this.imageRestoreOptionButton = document.getElementById("imageRestoreOptionButton");
        this.clickedImageId = null;
        this.clickedImageWidth = 0;
        this.clickedImageHeight = 0;
        this.MAX_IMAGE_WIDTH = 1000;
        this.MAX_IMAGE_HEIGHT = 999_999;
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

    /**
     * Toastify.js 팝업/알림 메시지
     */
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

    /**
     * sweetalert.js 팝업/알림 메시지
     */
    showSweetAlertNormalMessage(message, duration, callback) {
        if (duration) {
            Swal.fire({
                text: message,
                timer: duration, // 3초 후에 자동으로 사라짐
                timerProgressBar: true,
                onBeforeOpen: () => {
                    Swal.showLoading() // 로딩 아이콘 표시
                },
                onClose: () => {
                    clearInterval(duration) // 타이머 종료
                }
            }).then((result) => {
                if (callback) {
                    callback();
                }
            });
        } else {
            Swal.fire({
                text: message
            });
        }
        ;
    }

    showSweetAlertInfoMessage(message, duration, callback) {
        if (duration) {
            Swal.fire({
                icon: "info",
                text: message,
                timer: duration, // 3초 후에 자동으로 사라짐
                timerProgressBar: true,
                onBeforeOpen: () => {
                    Swal.showLoading() // 로딩 아이콘 표시
                },
                onClose: () => {
                    clearInterval(duration) // 타이머 종료
                }
            }).then((result) => {
                if (callback) {
                    callback();
                }
            });
        } else {
            Swal.fire({
                icon: "info",
                text: message
            });
        }
    }

    showSweetAlertWarningMessage(message, duration, callback) {
        if (duration) {
            Swal.fire({
                icon: "warning",
                text: message,
                timer: duration, // 3초 후에 자동으로 사라짐
                timerProgressBar: true,
                onBeforeOpen: () => {
                    Swal.showLoading() // 로딩 아이콘 표시
                },
                onClose: () => {
                    clearInterval(duration) // 타이머 종료
                }
            }).then((result) => {
                if (callback) {
                    callback();
                }
            });
        } else {
            Swal.fire({
                icon: "warning",
                text: message
            });
        }
    }

    showSweetAlertErrorMessage(message, duration, callback) {
        if (duration) {
            Swal.fire({
                icon: "error",
                text: message,
                timer: duration, // 3초 후에 자동으로 사라짐
                timerProgressBar: true,
                onBeforeOpen: () => {
                    Swal.showLoading() // 로딩 아이콘 표시
                },
                onClose: () => {
                    clearInterval(duration) // 타이머 종료
                }
            }).then((result) => {
                if (callback) {
                    callback();
                }
            });
        } else {
            Swal.fire({
                icon: "error",
                text: message
            });
        }
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

    quillScrollDownImage(quill, imageUrl) {
        const img = new Image();
        img.onload = function () {
            if (img) {
                const imageHeight = img.naturalHeight;
                quill.container.firstChild.scrollTop += imageHeight;
            }
        };
        img.src = imageUrl;
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
                // imageResize: {
                //     modules: ['Resize', 'DisplaySize', 'Toolbar']
                // },
                syntax: true,
                // imageCompressor: {
                //     quality: 1,
                //     maxWidth: 1000, // default
                //     maxHeight: 1000, // default
                //     ignoreImageTypes: ['image/gif']
                // },
                toolbar: {
                    container: [
                        ['bold', 'italic', 'underline', 'strike'],
                        ['blockquote', 'code-block'],
                        [{'header': 1}, {'header': 2}],
                        [{'list': 'ordered'}, {'list': 'bullet'}],
                        [{'script': 'sub'}, {'script': 'super'}],
                        [{'indent': '-1'}, {'indent': '+1'}],
                        [{'direction': 'rtl'}],
                        ['link', 'image', 'video', 'formula'],
                        [{'size': ['small', false, 'large', 'huge']}],
                        [{'header': [1, 2, 3, 4, 5, 6, false]}],
                        [{'color': []}, {'background': []}],
                        [{'font': []}],
                        [{'align': []}],
                        ['clean'],
                        ['emoji'],
                        ['codeBlock'],
                        ['trash'],
                        ['addVideo'],
                        ['editor_height']
                    ],
                    handlers: {
                        // Custom event handler for the link button
                        link: function (value) {
                            /**
                             * 해당 기능은 quill.editor 자체적으로 HTML 태그 및 인라인 CSS, 클래스 삽입을 막는다.
                             * 내가 원하는 코드를 dangerouslyPasteHTML 메서드를 통해 삽입해도, 에디터에 생략해서 포함 되며 제한적이다.
                             *
                             * 따라서, 내가 원하는 기능을 우회하여 직접 구현하는 것은 불가능하다. quill.js 개발팀에서 해당 기능을 제공 해주어야함.
                             *
                             * (Github issue) https://github.com/quilljs/quill/issues/4035
                             * (CodePen) https://codepen.io/jmkanmo/pen/yLryKKY
                             */
                            if (value) {
                                // If a URL is provided, perform your custom logic here
                                const url = prompt('Enter the link URL:');
                                const selection = quill.getSelection();
                                const dragText = quill.getText(selection.index, selection.length);

                                if (dragText && dragText !== '') {
                                    if (url) {
                                        this.quill.format('link', url);
                                    }
                                } else {
                                    // insert a tag
                                    if (!url || url === '') {
                                        return;
                                    }
                                    const data = `<a href="${url}">${url}</a>`;
                                    quill.clipboard.dangerouslyPasteHTML(quill.getSelection().index, data);
                                }
                            } else {
                                // If value is false, remove the link
                                this.quill.format('link', false);
                            }
                        },
                    },
                },
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
                    if (this.checkImageFileExtension(imgFile, ['jpg', 'JPG', 'jpeg', 'JPEG', 'png', 'PNG', 'gif', 'GIF'])) {
                        if (this.checkImageFileExtension(imgFile, ['gif', 'GIF']) && this.checkImageFileBySize(imgFile, this.MAX_IMAGE_UPLOAD_SIZE)) {
                            // if file extension is gif | GIF, 5MB가 넘지 않는 경우, 압축 진행 X
                            const fileReader = new FileReader();

                            fileReader.onload = (event) => {
                                const formData = new FormData();
                                const xhr = new XMLHttpRequest();
                                const spinner = this.loadingSpin({
                                    lines: 15,
                                    length: 2,
                                    width: 3,
                                    radius: 4,
                                    scale: 1,
                                    corners: 1,
                                    color: '#000',
                                    opacity: 0.25,
                                    rotate: 0,
                                    direction: 1,
                                    speed: 1,
                                    trail: 60,
                                    fps: 20,
                                    zIndex: 2e9,
                                    className: 'spinner',
                                    top: '30%',
                                    left: '50%',
                                    shadow: false,
                                    hwaccel: false,
                                    position: 'absolute'
                                }, "fileUploadLoading");
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
                                        this.showSweetAlertErrorMessage(responseValue);
                                        this.loadingStop(spinner, "fileUploadLoading");
                                    } else {
                                        quill.editor.insertEmbed(quill.getSelection().index, 'image', responseValue);
                                        this.quillScrollDownImage(quill, responseValue);
                                        setTimeout(function () {
                                            // 이미지를 찾아서 스타일을 변경합니다.
                                            const images = document.querySelectorAll('.ql-editor img');
                                            images.forEach(function (image) {
                                                const srcValue = image.src;
                                                image.style.cursor = 'pointer';
                                                image.setAttribute('title', image.src);
                                                image.setAttribute('id', `IMAGE-${srcValue}`);
                                                image.setAttribute('data-toggle', 'modal');
                                                image.setAttribute('data-target', '#image_option_modal');
                                            });
                                        }, 100);
                                        this.loadingStop(spinner, "fileUploadLoading");
                                    }
                                });

                                xhr.addEventListener("error", event => {
                                    this.showSweetAlertErrorMessage('오류가 발생하여 이미지 전송에 실패하였습니다.');
                                    this.loadingStop(spinner, "fileUploadLoading");
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
                                    const spinner = this.loadingSpin({
                                        lines: 15,
                                        length: 2,
                                        width: 3,
                                        radius: 4,
                                        scale: 1,
                                        corners: 1,
                                        color: '#000',
                                        opacity: 0.25,
                                        rotate: 0,
                                        direction: 1,
                                        speed: 1,
                                        trail: 60,
                                        fps: 20,
                                        zIndex: 2e9,
                                        className: 'spinner',
                                        top: '30%',
                                        left: '50%',
                                        shadow: false,
                                        hwaccel: false,
                                        position: 'absolute'
                                    }, "fileUploadLoading");
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
                                            this.showSweetAlertErrorMessage(responseValue);
                                            this.loadingStop(spinner, "fileUploadLoading");
                                        } else {
                                            quill.editor.insertEmbed(quill.getSelection().index, 'image', responseValue);
                                            quill.editor.insertEmbed(quill.getSelection().index + 1, 'block', '<p><br></p>');
                                            this.quillScrollDownImage(quill, responseValue);
                                            setTimeout(function () {
                                                // 이미지를 찾아서 스타일을 변경합니다.
                                                const images = document.querySelectorAll('.ql-editor img');
                                                images.forEach(function (image) {
                                                    const srcValue = image.src;
                                                    image.style.cursor = 'pointer';
                                                    image.setAttribute('title', image.src);
                                                    image.setAttribute('id', `IMAGE-${srcValue}`);
                                                    image.setAttribute('data-toggle', 'modal');
                                                    image.setAttribute('data-target', '#image_option_modal');
                                                });
                                            }, 100);
                                            this.loadingStop(spinner, "fileUploadLoading");
                                        }
                                    });

                                    xhr.addEventListener("error", event => {
                                        this.showSweetAlertErrorMessage('오류가 발생하여 이미지 전송에 실패하였습니다.');
                                        this.loadingStop(spinner, "fileUploadLoading");
                                    });

                                    formData.append("compressed_post_image", compressedImgFile);
                                    xhr.send(formData);
                                }
                                fileReader.readAsDataURL(compressedImgFile);
                            });
                        }
                    } else {
                        this.showSweetAlertWarningMessage("지정 된 이미지 파일 ('jpg', 'JPG', 'jpeg', 'JPEG', 'png', 'PNG', 'gif', 'GIF')만 업로드 가능합니다.");
                    }
                } catch (error) {
                    this.showSweetAlertErrorMessage("ERROR: " + error);
                }
            })
        });

        // quill editor click event handler
        document.getElementById("post_write_editor").addEventListener("click", evt => {
            const imgTag = evt.target.closest('img');

            if (imgTag) {
                const srcValue = imgTag.src;
                // check img src (Test Server | resource server, CDN)
                if (srcValue && (srcValue.toString().includes('http://192.168.35.98/images') || srcValue.toString().includes('https://www.zlzzlz-resource.info/images'))) {
                    imgTag.style.cursor = 'pointer';
                    imgTag.setAttribute('title', imgTag.src);
                    imgTag.setAttribute('id', `IMAGE-${srcValue}`);
                    imgTag.setAttribute('data-toggle', 'modal');
                    imgTag.setAttribute('data-target', '#image_option_modal');
                    this.clickedImageId = imgTag.getAttribute('id');
                    this.clickedImageWidth = imgTag.width;
                    this.clickedImageHeight = imgTag.height;
                    this.imageWidthInput.value = this.clickedImageWidth;
                    this.imageHeightInput.value = this.clickedImageHeight;
                }
            }
        });

        this.imageWidthInput.addEventListener("input", evt => {
            this.clickedImageWidth = this.imageWidthInput.value;
        });

        this.imageHeightInput.addEventListener("input", evt => {
            this.clickedImageHeight = this.imageHeightInput.value;
        });

        this.imageOptionSettingButton.addEventListener("click", evt => {
            if (this.clickedImageWidth > this.MAX_IMAGE_WIDTH) {
                this.showSweetAlertWarningMessage(`이미지 최대 너비는 ${this.MAX_IMAGE_WIDTH}px를 넘을 수 없습니다.`);
                return;
            }
            if (this.clickedImageHeight > this.MAX_IMAGE_HEIGHT) {
                this.showSweetAlertWarningMessage(`이미지 최대 높이는 ${this.MAX_IMAGE_HEIGHT}px를 넘을 수 없습니다.`);
                return;
            }
            this.imageWidthInput.value = this.clickedImageWidth;
            this.imageHeightInput.value = this.clickedImageHeight;
            document.getElementById(this.clickedImageId).width = this.imageWidthInput.value;
            document.getElementById(this.clickedImageId).height = this.imageHeightInput.value;
        });

        this.imageDeleteOptionButton.addEventListener("click", evt => {
            document.getElementById(this.clickedImageId).style.display = 'none';
        });

        this.imageRestoreOptionButton.addEventListener("click", evt => {
            document.getElementById(this.clickedImageId).style.display = 'block';
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

        /**
         * 프로토콜(XmlHttpRequest Mixed Content Error (HTTPS 내에서 HTTP 요청 불가) 등등 여러 제약사항 및 에러 발생으로 인해 동작 X)
         * TODO 추후에 Video CDN 도입 및 요청 로직 변경 등등 여러 대응책, 추가 개발 진행 고려 TOT
         *
         * 우선, Multipart Post 방식을 통한 video 업로드 진행
         *
         * front 작업 진행 (파일 추가 및 validation 체크 등등)
         *
         * http 요청 -> token(unique) 값 생성 + redis 저장 및 반환 /upload/video_token
         * 토큰 값을 토대로, 파일 서버에 tus protocol http 요청
         * 작업 완료 후에, src 값 및 img 태그 삽입
         */
        const addVideoButton = document.querySelector('.ql-addVideo');

        addVideoButton.innerHTML = `<svg id="Layer_1" data-name="Layer 1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 122.88 111.34"><title>video</title><path d="M23.59,0h75.7a23.68,23.68,0,0,1,23.59,23.59V87.75A23.56,23.56,0,0,1,116,104.41l-.22.2a23.53,23.53,0,0,1-16.44,6.73H23.59a23.53,23.53,0,0,1-16.66-6.93l-.2-.22A23.46,23.46,0,0,1,0,87.75V23.59A23.66,23.66,0,0,1,23.59,0ZM54,47.73,79.25,65.36a3.79,3.79,0,0,1,.14,6.3L54.22,89.05a3.75,3.75,0,0,1-2.4.87A3.79,3.79,0,0,1,48,86.13V50.82h0A3.77,3.77,0,0,1,54,47.73ZM7.35,26.47h14L30.41,7.35H23.59A16.29,16.29,0,0,0,7.35,23.59v2.88ZM37.05,7.35,28,26.47H53.36L62.43,7.38v0Zm32,0L59.92,26.47h24.7L93.7,7.35Zm31.32,0L91.26,26.47h24.27V23.59a16.32,16.32,0,0,0-15.2-16.21Zm15.2,26.68H7.35V87.75A16.21,16.21,0,0,0,12,99.05l.17.16A16.19,16.19,0,0,0,23.59,104h75.7a16.21,16.21,0,0,0,11.3-4.6l.16-.18a16.17,16.17,0,0,0,4.78-11.46V34.06Z"/></svg>`;
        addVideoButton.addEventListener('click', evt => {
            this.videoFileInput.click();
        });

        this.videoFileInput.addEventListener("change", evt => {
            const videoFile = this.videoFileInput.files[0];

            if (this.checkVideoFileExtension(videoFile.name) === false) {
                this.showSweetAlertWarningMessage("비디오 파일 확장자가 아닙니다.");
                return;
            } else if (this.checkVideoFileSize(videoFile, this.MAX_UPLOAD_VIDEO_FILE_SIZE) === false) {
                this.showSweetAlertWarningMessage("최대 업로드 파일 크기는 300MB 입니다.");
                return;
            }

            const fileReader = new FileReader();

            fileReader.onload = (event) => {
                const formData = new FormData();
                const xhr = new XMLHttpRequest();
                const spinner = this.loadingSpin({
                    lines: 15,
                    length: 2,
                    width: 3,
                    radius: 4,
                    scale: 1,
                    corners: 1,
                    color: '#000',
                    opacity: 0.25,
                    rotate: 0,
                    direction: 1,
                    speed: 1,
                    trail: 60,
                    fps: 20,
                    zIndex: 2e9,
                    className: 'spinner',
                    top: '30%',
                    left: '50%',
                    shadow: false,
                    hwaccel: false,
                    position: 'absolute'
                }, "fileUploadLoading");
                const uploadKeyDocument = document.getElementById("upload_key")
                let uploadKey = uploadKeyDocument.value;

                if (!uploadKey || uploadKey === '' || uploadKey === 'undefined') {
                    uploadKey = new Date().getTime();
                    uploadKeyDocument.value = uploadKey;
                }

                xhr.open("POST", `/video/upload/video/${uploadKey}`, true);
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

                xhr.addEventListener("loadend", event => {
                    let status = event.target.status;
                    const responseValue = event.target.responseText;

                    if ((status >= 400 && status <= 500) || (status > 500)) {
                        this.showSweetAlertErrorMessage(responseValue);
                        this.loadingStop(spinner, "fileUploadLoading");
                    } else {
                        // ex) https://www.zlzzlz-resource.info/images/1753056255/1706810674997/2024-02-01/1f66fef0-2257-4553-ac4d-e41f6adac0bb.png
                        const videoSrc = responseValue;
                        const iframeTag = '<iframe width="300" height="300" src="' + videoSrc + '" frameborder="0" allowfullscreen></iframe>';
                        const range = quill.getSelection();

                        if (range) {
                            quill.insertText(range.index + 1, '\n', Quill.sources.USER)
                            quill.clipboard.dangerouslyPasteHTML(range.index + 1, iframeTag);
                        }
                        this.loadingStop(spinner, "fileUploadLoading");
                    }
                });

                xhr.addEventListener("error", event => {
                    this.showSweetAlertErrorMessage('오류가 발생하여 비디오 업로드에 실패하였습니다.');
                    this.loadingStop(spinner, "fileUploadLoading");
                });

                formData.append("compressed_video", videoFile);
                xhr.send(formData);
            }
            fileReader.readAsDataURL(videoFile);
        });

        const editorHeightButton = document.querySelector('.ql-editor_height');
        editorHeightButton.innerHTML = `
            <div class="clear_fix editor_height_style" style="width: 110px; position: relative;">
                <label for="editorHeightInput" style="cursor: pointer" title="에디터 높이 (px)">
                    <svg version="1.1" style="width: 30px; height:20px; float:left;" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 41.04 122.88" style="enable-background:new 0 0 41.04 122.88" xml:space="preserve"><g><path d="M6.09,7.49c-2.07,0-3.75-1.68-3.75-3.75C2.34,1.68,4.02,0,6.09,0h28.86c2.07,0,3.75,1.68,3.75,3.75 c0,2.07-1.68,3.75-3.75,3.75H6.09L6.09,7.49z M6.09,122.88c-2.07,0-3.75-1.68-3.75-3.75s1.68-3.75,3.75-3.75h28.86 c2.07,0,3.75,1.68,3.75,3.75s-1.68,3.75-3.75,3.75H6.09L6.09,122.88z M22.03,109.61c-0.41,0.18-0.85,0.29-1.29,0.31l0,0l-0.09,0 l0,0l-0.09,0h0h-0.09h0l-0.09,0l0,0l-0.09,0l0,0c-0.44-0.03-0.88-0.13-1.29-0.31l-0.01,0l-0.01-0.01l-0.07-0.03l-0.01-0.01 l-0.07-0.03l-0.02-0.01l-0.01-0.01c-0.24-0.12-0.47-0.27-0.68-0.45L1.4,95.55c-1.61-1.29-1.86-3.65-0.57-5.25 c1.29-1.61,3.65-1.86,5.25-0.57l10.73,8.67l-0.02-71L6.08,36.04c-1.61,1.29-3.96,1.04-5.25-0.57c-1.29-1.61-1.04-3.96,0.57-5.25 L18.12,16.7c0.21-0.18,0.44-0.33,0.68-0.45l0.01-0.01l0.02-0.01l0.07-0.03l0.01-0.01l0.07-0.03l0.01-0.01l0.01,0 c0.41-0.18,0.85-0.29,1.29-0.31l0,0l0.09,0l0,0l0.09,0h0h0.09h0l0.09,0l0,0l0.09,0l0,0c0.44,0.03,0.88,0.13,1.29,0.31l0.01,0 l0.01,0.01l0.07,0.03l0.01,0.01l0.07,0.03l0.02,0.01l0.01,0.01c0.24,0.12,0.47,0.27,0.68,0.45l16.72,13.52 c1.61,1.29,1.86,3.65,0.57,5.25c-1.29,1.61-3.65,1.86-5.25,0.57l-10.7-8.65l0.02,70.97l10.68-8.63c1.61-1.29,3.96-1.04,5.25,0.57 c1.29,1.61,1.04,3.96-0.57,5.25l-16.72,13.52c-0.21,0.18-0.44,0.33-0.68,0.45l-0.01,0.01l-0.02,0.01l-0.07,0.03l-0.01,0.01 l-0.07,0.03l-0.01,0.01L22.03,109.61L22.03,109.61z"/></g></svg>
                </label>
                <input type="number" id="editorHeightInput" style="float: right; width: 80px; height: 20px;" min="1" max="999999">
                <button type="button" class="common_orange_button" id="editorHeightSettingButton" style="position: absolute; right: -40px; border: 1px solid #e0763c; background-color: #e0763c; width: 35px; font-size: 11px; top: -3px;">적용</button>
            </div>`;

        const postWriteEditor = document.getElementById("post_write_editor");
        const editorHeightInput = document.getElementById("editorHeightInput");
        const editorHeightSettingButton = document.getElementById("editorHeightSettingButton");
        editorHeightInput.value = postWriteEditor.clientHeight;

        editorHeightSettingButton.addEventListener('click', evt => {
            const heightValue = editorHeightInput.value;
            const maxHeightValue = editorHeightInput.max;

            if (heightValue && maxHeightValue) {
                if (parseInt(heightValue) > 0) {
                    if (parseInt(heightValue) > parseInt(maxHeightValue)) {
                        this.showSweetAlertWarningMessage(`에디터 높이는 ${maxHeightValue}px를 초과할 수 없습니다.`);
                    } else {
                        postWriteEditor.style.height = `${heightValue}px`;
                    }
                } else {
                    this.showSweetAlertWarningMessage("에디터 높이는 1px보다 작을 수 없습니다.");
                }
            }
        });
        return quill;
    }

    getCurrentDateYYYYMMDD() {
        const currentDate = new Date();
        const year = currentDate.getFullYear();
        const month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // Months are zero-based
        const day = currentDate.getDate().toString().padStart(2, '0');
        const formattedDate = `${year}-${month}-${day}`;
        return formattedDate;
    }

    getFileExtension(filename) {
        return filename.slice(((filename.lastIndexOf(".") - 1 >>> 0) + 2)).toLowerCase();
    }

    deleteSpace(str) {
        if (!str) {
            return str;
        }
        return str.split('').filter(char => char !== ' ').join('');
    }

    generateVodName(fileName) {
        return `${new Date().getTime()}_${this.generateString(30)}_${fileName}`;
    }

    generateString(length) {
        // program to generate random strings
        // declare all characters
        const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let result = '';
        const charactersLength = characters.length;
        for (let i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        return result;
    }

    checkVideoFileSize(videoFile, maxFileSize) {
        if (videoFile) {
            if (videoFile.size >= maxFileSize) {
                return false;
            }
        }
        return true
    }

    checkVideoFileExtension(filename) {
        const allowedExtensions = ['mp4', 'avi', 'mkv', 'wmv', 'flv', 'mov', 'webm', '3gp'];

        // 파일 이름이 null이거나 빈 문자열인 경우 유효하지 않음
        if (!filename) {
            return false;
        }

        // 파일 이름에서 마지막 점('.') 이후의 문자열 추출
        const fileExtension = filename.slice(((filename.lastIndexOf(".") - 1 >>> 0) + 2)).toLowerCase();

        // 허용 가능한 확장자 목록과 비교하여 유효성 검사
        return allowedExtensions.includes(fileExtension);
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
            if (!this.checkImageFileExtension(imgFile, ['jpg', 'JPG', 'jpeg', 'JPEG', 'png', 'PNG', 'gif', 'GIF'])) {
                this.showSweetAlertWarningMessage("지정 된 이미지 파일 ('jpg', 'JPG', 'jpeg', 'JPEG', 'png', 'PNG', 'gif', 'GIF')만 업로드 가능합니다.");
                return false;
            } else if (!this.checkImageFileBySize(imgFile, this.MAX_IMAGE_UPLOAD_SIZE)) {
                this.showSweetAlertWarningMessage('최대 업로드 파일 크기는 5MB 입니다.');
                return false;
            }
        }
        return true;
    }

    checkImageFile(imgFile, size) {
        if (imgFile) {
            if (!this.checkImageFileExtension(imgFile, ['jpg', 'JPG', 'jpeg', 'JPEG', 'png', 'PNG', 'gif', 'GIF'])) {
                this.showSweetAlertWarningMessage("지정 된 이미지 파일 ('jpg', 'JPG', 'jpeg', 'JPEG', 'png', 'PNG', 'gif', 'GIF')만 업로드 가능합니다.");
                return false;
            } else if (!this.checkImageFileBySize(imgFile, size * 1024 * 1024)) {
                this.showSweetAlertWarningMessage('최대 업로드 파일 크기는 ' + size + 'MB 입니다.');
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