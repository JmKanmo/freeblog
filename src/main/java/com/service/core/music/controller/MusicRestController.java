package com.service.core.music.controller;

import com.service.core.music.dto.MusicPagingResponseDto;
import com.service.core.music.paging.MusicSearchPagingDto;
import com.service.core.music.service.MusicService;
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


@Tag(name = "뮤직", description = "뮤직 관련 REST API")
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
@Slf4j
public class MusicRestController {
    private final MusicService musicService;

    @Operation(summary = "뮤직 리스트 반환", description = "뮤직 리스트 데이터 반환 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "뮤직 리스트 데이터 반환 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 뮤직 리스트 데이터 반환 실패")
    })
    @GetMapping("/play-list")
    public ResponseEntity<MusicPagingResponseDto> searchMusicPlayList(@RequestParam(value = "categoryId", required = false, defaultValue = "0") Long categoryId,
                                                                      @ModelAttribute MusicSearchPagingDto musicSearchPagingDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(MusicPagingResponseDto.success(musicService.searchMusicDto(musicSearchPagingDto, categoryId)));
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-searchMusicPlayList] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MusicPagingResponseDto.fail(exception));
        }
    }
}
