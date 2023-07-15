package com.service.core.notice.controller;

import com.service.core.notice.domain.NoticeAlarm;
import com.service.core.notice.service.NoticeService;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Tag(name = "공지사항", description = "포스트 관련 REST API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
@Slf4j
public class NoticeRestController {
    private final NoticeService noticeService;

    @Operation(summary = "공지사항 알람 발생 확인", description = "공지사항 알람 발생 확인 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 알람 발생 확인 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 공지사항 알람 발생 확인 실패")
    })
    @GetMapping("/check-alarm")
    public ResponseEntity<Boolean> checkNoticeAlarm(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @CookieValue(value = "notice-alarm", defaultValue = ConstUtil.UNDEFINED) String cookieValue) {
        try {
            /**
             * 가장 최근에 올라온 공지사항을 기준으로 진행
             * 공지사항 ID & 공지사항체크여부(Read | UnRead)
             * 35453543&Read
             * 35453543&UnRead
             *
             * true: (알림 발생)
             * false: (알림 미발생)
             */
            boolean isNoticeAlarm = noticeService.checkNoticeAlarm();

            if (isNoticeAlarm) {
                // 공지사항이 있으면 알림 발생 여부 체크
                NoticeAlarm noticeAlarm = noticeService.findRecentNoticeAlarm();

                if (noticeAlarm == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(false);
                }

                if (cookieValue.equals(ConstUtil.UNDEFINED)) {
                    /**
                     * 개수 상관 없이 제일 최근에 생성된 NoticeAlarm id로 쿠키 지정
                     * cookie age는 1day 업데이트, UnRead 처리
                     */
                    cookieValue = String.valueOf(noticeAlarm.getId()) + "&" + "UnRead";
                    Cookie cookie = new Cookie("notice-alarm", cookieValue);
                    cookie.setMaxAge(24 * 60 * 60);
                    httpServletResponse.addCookie(cookie);
                } else {
                    /**
                     * 가장 최근에 생성된 NoticeAlarm의 id가 cookie의 id 값과 일치하는지 체크
                     * 일치할 경우, Read 여부 조사
                     *
                     * 일치하지 않은 경우는 새로운 공지사항이 올라왔다는것
                     * 따라서 cookie의 id를 새로 update하고 age도 새롭게 1day 업데이트 한다. UnRead 진행
                     */
                    String[] parsed = cookieValue.split("&");

                    if (parsed.length < 2) {
                        cookieValue = String.valueOf(noticeAlarm.getId()) + "&" + "UnRead";
                        Cookie cookie = new Cookie("notice-alarm", cookieValue);
                        cookie.setMaxAge(24 * 60 * 60);
                        httpServletResponse.addCookie(cookie);
                        return ResponseEntity.status(HttpStatus.OK).body(false);
                    }

                    String noticeAlarmId = parsed[0];

                    if (!String.valueOf(noticeAlarm.getId()).equals(noticeAlarmId)) {
                        cookieValue = String.valueOf(noticeAlarm.getId()) + "&" + "UnRead";
                        Cookie cookie = new Cookie("notice-alarm", cookieValue);
                        cookie.setMaxAge(24 * 60 * 60);
                        httpServletResponse.addCookie(cookie);
                    } else {
                        String readStr = parsed[1];

                        if (readStr.equals("Read")) {
                            return ResponseEntity.status(HttpStatus.OK).body(false);
                        }
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body(true);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(false);
            }
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-checkNoticeAlarm] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @Operation(summary = "공지사항 알람 읽기", description = "공지사항 알람 읽기 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 알람 읽기 성공"),
            @ApiResponse(responseCode = "500", description = "데이터베이스 연결 불량, 쿼리 동작 실패 등으로 공지사항 알람 읽기 실패")
    })
    @PostMapping("/read-alarm")
    public ResponseEntity<Boolean> readNoticeAlarm(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @CookieValue(value = "notice-alarm", defaultValue = ConstUtil.UNDEFINED) String cookieValue) {
        try {
            /**
             * 가장 최근에 올라온 공지사항을 기준으로 진행
             * 공지사항 ID & 공지사항체크여부(Read | UnRead)
             * 35453543&Read
             * 35453543&UnRead
             *
             * true: (읽기 성공)
             * false: (읽기 실패)
             */
            boolean isNoticeAlarm = noticeService.checkNoticeAlarm();

            if (isNoticeAlarm) {
                // 공지사항이 있으면 알림 발생 여부 체크
                NoticeAlarm noticeAlarm = noticeService.findRecentNoticeAlarm();

                if (noticeAlarm == null) {
                    return ResponseEntity.status(HttpStatus.OK).body(false);
                }

                if (cookieValue.equals(ConstUtil.UNDEFINED)) {
                    cookieValue = String.valueOf(noticeAlarm.getId()) + "&" + "Read";
                    Cookie cookie = new Cookie("notice-alarm", cookieValue);
                    cookie.setMaxAge(24 * 60 * 60);
                    httpServletResponse.addCookie(cookie);
                } else {
                    String[] parsed = cookieValue.split("&");

                    if (parsed.length < 2) {
                        cookieValue = String.valueOf(noticeAlarm.getId()) + "&" + "Read";
                        Cookie cookie = new Cookie("notice-alarm", cookieValue);
                        cookie.setMaxAge(24 * 60 * 60);
                        httpServletResponse.addCookie(cookie);
                    }

                    String noticeAlarmId = parsed[0];
                    String readStr = parsed[1];

                    if (!noticeAlarmId.equals(String.valueOf(noticeAlarm.getId())) || !readStr.equals("Read")) {
                        cookieValue = String.valueOf(noticeAlarm.getId()) + "&" + "Read";
                        Cookie cookie = new Cookie("notice-alarm", cookieValue);
                        cookie.setMaxAge(24 * 60 * 60);
                        httpServletResponse.addCookie(cookie);
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body(true);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(false);
            }
        } catch (Exception exception) {
            if (BlogUtil.getErrorMessage(exception) == ConstUtil.UNDEFINED_ERROR) {
                log.error("[freeblog-readNoticeAlarm] exception occurred ", exception);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
