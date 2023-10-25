package com.service.core.music.controller;

import com.service.core.blog.dto.BlogInfoDto;
import com.service.core.blog.service.BlogService;
import com.service.core.error.constants.ServiceExceptionMessage;
import com.service.core.error.model.UserManageException;
import com.service.core.music.dto.MusicCategoryDto;
import com.service.core.music.dto.MusicPagingResponseDto;
import com.service.core.music.dto.UserMusicCategoryDto;
import com.service.core.music.model.UserMusicInput;
import com.service.core.music.paging.MusicSearchPagingDto;
import com.service.core.music.service.MusicCategoryService;
import com.service.core.music.service.MusicService;
import com.service.core.music.service.UserMusicCategoryService;
import com.service.core.music.service.UserMusicService;
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

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@Tag(name = "뮤직", description = "뮤직 관련 REST API")
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
@Slf4j
public class MusicRestController {
    private final BlogService blogService;
    private final MusicService musicService;
    private final MusicCategoryService musicCategoryService;
    private final UserMusicCategoryService userMusicCategoryService;
    private final UserMusicService userMusicService;

    @Operation(summary = "뮤직 리스트 반환", description = "뮤직 리스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뮤직 리스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 뮤직 리스트 데이터 반환 실패")
    })
    @GetMapping("/play-list")
    public ResponseEntity<MusicPagingResponseDto> searchMusicPlayList(@RequestParam(value = "categoryId", required = false, defaultValue = "0") Long categoryId,
                                                                      @ModelAttribute MusicSearchPagingDto musicSearchPagingDto) {
        try {
            MusicCategoryDto musicCategoryDto = musicCategoryService.findMusicCategoryDtoById(categoryId);

            if (BlogUtil.checkEmptyOrUndefinedStr(musicCategoryDto.getName()) || musicCategoryDto.getName().equals(ConstUtil.TOTAL)) {
                categoryId = 0L;
            }
            return ResponseEntity.status(HttpStatus.OK).body(MusicPagingResponseDto.success(musicService.searchMusicDto(musicSearchPagingDto, categoryId)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-searchMusicPlayList] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MusicPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "뮤직 다운로드 리스트 반환", description = "뮤직 다운로드 리스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뮤직 다운로드 리스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 뮤직 다운로드 리스트 데이터 반환 실패")
    })
    @GetMapping("/download-list")
    public ResponseEntity<MusicPagingResponseDto> searchMusicDownloadList(Principal principal, @RequestParam(value = "categoryId", required = false, defaultValue = "0") Long categoryId,
                                                                          @ModelAttribute MusicSearchPagingDto musicSearchPagingDto) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
            }
            UserMusicCategoryDto userMusicCategoryDto = userMusicCategoryService.findUserMusicCategoryDtoByIdOrElseNull(categoryId);
            BlogInfoDto blogInfoDto = blogService.findBlogInfoDtoByEmail(principal.getName());

            if (userMusicCategoryDto == null) {
                categoryId = 0L;
            }
            return ResponseEntity.status(HttpStatus.OK).body(MusicPagingResponseDto.success(userMusicService.searchUserMusicDto(musicSearchPagingDto, categoryId, blogInfoDto.getId())));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-searchMusicDownloadList] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MusicPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "뮤직 다운로드", description = "뮤직 다운로드 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뮤직 다운로드 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 뮤직 다운로드 실패")
    })
    @PostMapping("/download")
    public ResponseEntity<MusicPagingResponseDto> downloadMusic(Principal principal, @RequestBody List<UserMusicInput> userMusicInputList) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
            }
            return ResponseEntity.status(HttpStatus.OK).body(MusicPagingResponseDto.success(userMusicService.downloadMusic(principal.getName(), userMusicInputList)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-downloadMusic] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MusicPagingResponseDto.fail(exception));
        }
    }

    @Operation(summary = "뮤직 삭제", description = "뮤직 삭제 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뮤직 다운로드 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 뮤직 다운로드 실패")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<MusicPagingResponseDto> deleteMusic(Principal principal, @RequestBody @Valid List<UserMusicInput> userMusicInputList) {
        try {
            if ((principal == null || principal.getName() == null)) {
                throw new UserManageException(ServiceExceptionMessage.NO_LOGIN_ACCESS);
            }
            userMusicService.deleteMusic(userMusicInputList);
            return ResponseEntity.status(HttpStatus.OK).body(MusicPagingResponseDto.success("OK"));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-deleteMusic] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MusicPagingResponseDto.fail(exception));
        }
    }
}
