package com.service.core.user.controller;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserAuthException;
import com.service.core.user.service.UserService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Tag(name = "사용자", description = "사용자 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Slf4j
public class UserRestController {
    private final UserService userService;

    @Operation(summary = "이메일 사용가능 여부 확인", description = "이메일 사용 가능 여부 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 사용 가능"),
            @ApiResponse(responseCode = "500", description = "이미 사용 중으로 불가능")
    })
    @ResponseBody
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam(value = "email", required = false, defaultValue = "") String email) {
        try {
            userService.checkSameEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 이메일 입니다.");
        } catch (Exception exception) {
            log.error("[freeblog-checkEmail] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BlogUtil.getErrorMessage(exception));
        }
    }

    @Operation(summary = "id 사용가능 여부 확인", description = "id 사용 가능 여부 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "id 사용 가능"),
            @ApiResponse(responseCode = "500", description = "이미 사용 중으로 불가능")
    })
    @ResponseBody
    @GetMapping("/check-id")
    public ResponseEntity<String> checkId(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        try {
            userService.checkSameId(id);
            return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 id 입니다.");
        } catch (Exception exception) {
            log.error("[freeblog-checkId] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BlogUtil.getErrorMessage(exception));
        }
    }

    @Operation(summary = "사용자 프로필 이미지 업로드", description = "사용자 프로필 이미지 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 사용자 프로필 이미지 업로드 실패")
    })
    @PostMapping("/upload/profile-image")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("profile_image_file_input") MultipartFile multipartFile,
                                                     @RequestParam(value = "id", required = false, defaultValue = ConstUtil.UNDEFINED) String id, Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                throw new UserAuthException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            String profileImageSrc = userService.uploadAwsS3ProfileImageById(multipartFile, id, principal);
            return ResponseEntity.status(HttpStatus.OK).body(profileImageSrc);
        } catch (Exception exception) {
            log.error("[freeblog-uploadProfileImage] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("사용자 프로필 이미지 업로드에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

    @Operation(summary = "사용자 프로필 이미지 삭제", description = "사용자 프로필 이미지 삭제 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 삭제 완료"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 조회,삭제 작업 실패 등의 이유로 사용자 프로필 이미지 삭제 실패")
    })
    @DeleteMapping("/remove/profile-image")
    public ResponseEntity<String> removeProfileImage(@RequestParam(value = "id", required = false, defaultValue = ConstUtil.UNDEFINED) String id, Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                throw new UserAuthException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            userService.removeProfileImageById(id, principal);
            return ResponseEntity.status(HttpStatus.OK).body("사용자 프로필 이미지가 삭제되었습니다.");
        } catch (Exception exception) {
            log.error("[freeblog-removeProfileImage] exception occurred ", exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("사용자 프로필 이미지 삭제에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }
}
