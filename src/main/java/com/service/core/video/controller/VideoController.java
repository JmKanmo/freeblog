package com.service.core.video.controller;

import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserManageException;
import com.service.core.video.dto.VideoTokenDto;
import com.service.core.video.service.VideoService;
import com.service.util.BlogUtil;
import com.service.util.ConstUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video")

@Slf4j
public class VideoController {
    private final VideoService videoService;

    @Operation(summary = "비디오 업로드", description = "비디오 업로드 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비디오 업로드 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 비디오 업로드 실패")
    })
    @PostMapping("/upload/video/{uploadKey}")
    public ResponseEntity<String> uploadVideo(@RequestParam("compressed_video") MultipartFile multipartFile, Principal principal, @PathVariable String uploadKey) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            return ResponseEntity.status(HttpStatus.OK).body(videoService.uploadSftpVideo(multipartFile, uploadKey));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-uploadVideo] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("비디오 업로드에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception)));
        }
    }

    @Operation(summary = "비디오 토큰 발급", description = "비디오 토큰 발급 수행 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비디오 토큰 발급 완료"),
            @ApiResponse(responseCode = "500", description = "네트워크, 데이터베이스 저장 실패 등의 이유로 비디오 토큰 발급 실패")
    })
    @GetMapping("/upload/video_token")
    public ResponseEntity<VideoTokenDto> uploadVideoToken(Principal principal) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            return ResponseEntity.status(HttpStatus.OK).body(VideoTokenDto.success(videoService.generateVideoToken(principal)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-uploadVideoToken] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(VideoTokenDto.fail(String.format("비디오 업로드 토큰 생성에 실패하였습니다. %s", BlogUtil.getErrorMessage(exception))));
        }
    }
}
