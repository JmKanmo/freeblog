class SettingsController extends HeaderController {
    constructor() {
        super();

        this.profileImageForm = document.getElementById("profile_image_form");

        this.userEmail = document.getElementById("user_basic_info_email");
        this.userId = document.getElementById("user_basic_info_id");
        this.defaultUserProfileImage = document.getElementById("default_user_profile_image");
        this.userProfileRegisterButton = document.getElementById("profile_image_register_button");
        this.userProfileRemoveButton = document.getElementById("profile_image_remove_button");
        this.userProfileImageInput = document.getElementById("profile_image_file_input");

        this.greetingShowButton = document.getElementById("greeting_show_button");
        this.greetingsHiddenBlock = document.getElementById("greetings_hidden_block");
        this.greetingsCloseButton = document.getElementById("greeting_hidden_block_close_button");

        this.introShowButton = document.getElementById("intro_show_button");
        this.userBasicInfoEditButton = document.getElementById("user_basic_info_edit_button");

        this.emailAuthButton = document.getElementById("email_auth_button");
        this.emailNotAuthButton = document.getElementById("email_not_auth_button");

        this.userSocialInfoEditButton = document.getElementById("user_social_info_edit_button");

        this.passwordChangeButton = document.getElementById("password_change_button");

        this.withDrawButton = document.getElementById("with_draw_button");

        this.emailAuthTime = document.getElementById("user_email_auth_time");

        this.isSubmitFlag = false;
        this.isImageUploadFlag = false;
        this.isImageDeleteFlag = false;

        this.musicHeaderController = new MusicHeaderController();
    }

    initSettingsController() {
        this.initEventListener();
        this.musicHeaderController.initMusicPlayer();
    }

    initEventListener() {
        this.greetingShowButton.addEventListener("click", evt => {
            if (this.greetingsHiddenBlock.style.display == '' || this.greetingsHiddenBlock.style.display == 'none') {
                this.greetingsHiddenBlock.style.display = 'block';
            } else {
                this.greetingsHiddenBlock.style.display = 'none';
            }
        });

        this.greetingsCloseButton.addEventListener("click", evt => {
            if (this.greetingsHiddenBlock.style.display == 'block') {
                this.greetingsHiddenBlock.style.display = 'none';
            }
        });

        this.introShowButton.addEventListener("click", evt => {
            this.openPopUp(988, 750, '/user/intro', 'popup')
        });

        this.userBasicInfoEditButton.addEventListener("click", evt => {
            this.openPopUp(988, 750, '/user/update/basic-info', 'popup');
        });

        if (this.emailAuthButton) {
            this.emailAuthButton.addEventListener("click", () => {
                if (this.emailAuthTime.value === "") {
                    this.showToastMessage("이메일 인증이 완료 된 계정입니다.");
                } else {
                    this.showToastMessage(`${this.emailAuthTime.value} 시간에 이메일 인증이 완료 된 계정입니다.`);
                }
            });
        } else if (this.emailNotAuthButton) {
            this.emailNotAuthButton.addEventListener("click", evt => {
                if (confirm("해당 이메일 주소로 인증 메일을 전송하겠습니까?")) {
                    const xhr = new XMLHttpRequest();
                    const email = document.getElementById("user_basic_info_email").value;

                    if (!email) {
                        this.showToastMessage("이메일 정보를 찾을 수 없습니다.");
                        return;
                    }

                    xhr.open("GET", `/email/send/auth?email=${email}`, true);

                    xhr.addEventListener("loadend", event => {
                        let status = event.target.status;
                        const responseValue = event.target.responseText;

                        if ((status >= 400 && status <= 500) || (status > 500)) {
                            this.showToastMessage(responseValue);
                        } else {
                            this.showToastMessage(`이메일이 정상적으로 전송되었습니다.`);
                            this.authTargetEmailButton.disabled = true;
                        }
                    });

                    xhr.addEventListener("error", event => {
                        this.showToastMessage('이메일 전송에 실패하였습니다.');
                    });
                    xhr.send();
                }
            });
        }

        this.userProfileImageInput.addEventListener("change", evt => {
            if (this.isImageUploadFlag === true) {
                this.showToastMessage("이미지 업로드를 진행 중입니다.");
                return;
            }

            const imgFile = evt.target.files[0];

            if (this.checkImageFileExtension(imgFile, ['jpg', 'jpeg', 'png', 'gif', 'GIF'])) {
                if (this.checkImageFileExtension(imgFile, ['gif', 'GIF']) && this.checkImageFileBySize(imgFile, 300 * 1024)) {
                    // if file extension is gif | GIF, 300KB가 넘지 않는 경우, 압축 진행 X
                    this.#uploadImage(imgFile);
                } else {
                    this.getCompressedImageFile(imgFile).then(compressedImgFile => {
                        this.#uploadImage(compressedImgFile);
                    });
                }
            } else {
                this.showToastMessage("지정 된 이미지 파일 ('jpg', 'jpeg', 'png', 'gif', 'GIF')만 업로드 가능합니다.");
                this.isImageUploadFlag = false;
            }
        });

        this.userProfileRegisterButton.addEventListener("click", evt => {
            this.userProfileImageInput.click();
        });

        this.userProfileRemoveButton.addEventListener("click", evt => {
            if (this.isImageDeleteFlag === true) {
                this.showToastMessage("프로필 이미지를 삭제 중입니다.");
                return;
            }

            if (confirm("프로필 이미지를 삭제하시겠습니까?")) {
                if (this.defaultUserProfileImage.src.includes(this.getDefaultUserProfileThumbnail()
                    .split('/')[this.getDefaultUserProfileThumbnail().split('/').length - 1])) {
                    this.showToastMessage("현재 프로필 이미지가 지정되어 있지 않습니다.");
                    this.isImageDeleteFlag = false;
                    return;
                }
                this.removeUserProfileImage();
                this.removeHeaderUserProfileImage();
                this.#removeRemoteUserProfileImage();
                this.isImageDeleteFlag = true;
            }
        });

        this.userSocialInfoEditButton.addEventListener("click", evt => {
            this.openPopUp(1080, 500, '/user/update/social-address', 'popup');
        });

        this.passwordChangeButton.addEventListener("click", evt => {
            if (confirm("해당 이메일 주소로 비밀번호 재설정 메일을 전송하겠습니까?")) {
                const xhr = new XMLHttpRequest();
                const email = this.userEmail.value;

                if (!email) {
                    this.showToastMessage("이메일 정보가 존재하지 않습니다.");
                    return;
                }

                xhr.open("GET", `/email/send/find-password?email=${email}`, true);

                xhr.addEventListener("loadend", event => {
                    let status = event.target.status;
                    const responseValue = event.target.responseText;

                    if ((status >= 400 && status <= 500) || (status > 500)) {
                        this.showToastMessage(responseValue);
                    } else {
                        this.showToastMessage(`이메일이 정상적으로 전송되었습니다. 재전송을 원할 시에 새로고침 후 시도하세요.`);
                        this.passwordChangeButton.disabled = true;
                    }
                });

                xhr.addEventListener("error", event => {
                    this.showToastMessage('이메일 전송에 실패하였습니다.');
                });
                xhr.send();
            }
        });

        this.withDrawButton.addEventListener("click", evt => {
            this.openPopUp(1080, 500, '/user/withdraw', 'popup');
        });
    }

    #uploadImage(imgFile) {
        if (this.checkImageFile(imgFile)) {
            const fileReader = new FileReader();

            fileReader.onload = (event) => {
                const xhr = new XMLHttpRequest();
                const id = this.userId.value;
                const formData = new FormData(this.profileImageForm);

                xhr.open("POST", `/user/upload/profile-image?id=${id}`, true);
                xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

                xhr.addEventListener("loadend", event => {
                    let status = event.target.status;
                    const responseValue = event.target.responseText;

                    if ((status >= 400 && status <= 500) || (status > 500)) {
                        this.showToastMessage(responseValue);
                        this.removeUserProfileImage();
                    } else {
                        this.showToastMessage('프로필 썸네일 이미지 저장에 성공하였습니다.');
                        this.defaultUserProfileImage.src = responseValue;
                        this.setHeaderUserProfileImage(responseValue);
                    }
                    this.isImageUploadFlag = false;
                });

                xhr.addEventListener("error", event => {
                    this.showToastMessage('오류가 발생하여 이미지 전송에 실패하였습니다.');
                    this.removeUserProfileImage();
                    this.isImageUploadFlag = false;
                });
                formData.set("compressed_user_profile_image", imgFile);
                xhr.send(formData);
            }
            fileReader.readAsDataURL(imgFile);
            this.isImageUploadFlag = true;
        } else {
            this.isImageUploadFlag = false;
            this.removeUserProfileImage();
        }
    }

    removeUserProfileImage() {
        let fileBuffer = new DataTransfer();
        this.userProfileImageInput.files = fileBuffer.files; // <-- according to your file input reference
        this.defaultUserProfileImage.src = this.getDefaultUserProfileThumbnail();
    }

    #removeRemoteUserProfileImage() {
        const xhr = new XMLHttpRequest();
        const id = this.userId.value;

        xhr.open('DELETE', `/user/remove/profile-image?id=${id}`, true);
        xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));

        xhr.addEventListener("loadend", event => {
            let status = event.target.status;
            const responseValue = event.target.responseText;

            if ((status >= 400 && status <= 500) || (status > 500)) {
                this.showToastMessage(responseValue);
            } else {
                this.showToastMessage(`프로필 이미지가 정상적으로 삭제 되었습니다.`);
            }
            this.isImageDeleteFlag = false;
        });

        xhr.addEventListener("error", event => {
            this.showToastMessage(`프로필 이미지 삭제에 실패하였습니다.`);
        });

        xhr.send();
    }
}

// Execute all functions
document.addEventListener("DOMContentLoaded", () => {
    const settingsController = new SettingsController();
    settingsController.initSettingsController();
});