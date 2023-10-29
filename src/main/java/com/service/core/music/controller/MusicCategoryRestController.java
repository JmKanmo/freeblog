package com.service.core.music.controller;

import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserManageException;
import com.service.core.music.dto.MusicPagingResponseDto;
import com.service.core.music.service.MusicCategoryService;
import com.service.core.music.service.UserMusicCategoryService;
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

import java.security.Principal;

@Tag(name = "뮤직 카테고리", description = "뮤직 카테고리 관련 REST API")
@RestController
@RequestMapping("/music-category")
@RequiredArgsConstructor
@Slf4j
public class MusicCategoryRestController {
    private final BlogService blogService;
    private final MusicCategoryService musicCategoryService;
    private final UserMusicCategoryService userMusicCategoryService;

    @Operation(summary = "뮤직 카테고리 리스트 반환", description = "뮤직 카테고리 리스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뮤직 카테고리 리스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 뮤직 카테고리 리스트 데이터 반환 실패")
    })
    @GetMapping("/list")
    public ResponseEntity<MusicPagingResponseDto> searchMusicCategoryList(Principal principal) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            return ResponseEntity.status(HttpStatus.OK).body(MusicPagingResponseDto.success(musicCategoryService.searchMusicCategoryDto()));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-searchMusicCategoryList] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MusicPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "사용자 뮤직 카테고리 리스트 반환", description = "사용자 뮤직 카테고리 리스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 뮤직 카테고리 리스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 사용자 뮤직 카테고리 리스트 데이터 반환 실패")
    })
    @GetMapping("/user-list")
    public ResponseEntity<MusicPagingResponseDto> searchUserMusicCategoryList(Principal principal) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NOT_LOGIN_STATUS_ACCESS);
            }
            BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoByEmail(principal.getName());
            return ResponseEntity.status(HttpStatus.OK).body(MusicPagingResponseDto.success(userMusicCategoryService.searchUserMusicCategoryDto(blogInfoDto.getId())));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-searchUserMusicCategoryList] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MusicPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "사용자 뮤직 카테고리 리스트 반환", description = "사용자 뮤직 카테고리 리스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 뮤직 카테고리 리스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 사용자 뮤직 카테고리 리스트 데이터 반환 실패")
    })
    @GetMapping("/open/user-list/{blogId}")
    public ResponseEntity<MusicPagingResponseDto> openSearchUserMusicCategoryList(@PathVariable Long blogId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(MusicPagingResponseDto.success(userMusicCategoryService.searchUserMusicCategoryDto(blogId)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-openSearchUserMusicCategoryList] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MusicPagingResponseDto.fail(exception));
        }
    }
}
