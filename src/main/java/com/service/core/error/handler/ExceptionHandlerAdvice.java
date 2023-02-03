package com.service.core.error.handler;

import com.service.core.error.dto.ExceptionDto;
import com.service.util.BlogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.thymeleaf.exceptions.TemplateEngineException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fileSizeLimitExceededHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("error", BlogUtil.getErrorMessage(exception));
        log.error("[freeblog-fileSizeLimitExceededHandler] MaxUploadSizeExceededException occurred ", exception);
        return "error/error-page";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> httpRequestMethodNotSupportedHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        log.error("[freeblog-httpRequestMethodNotSupportedHandler] HttpRequestMethodNotSupportedException occurred ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionDto.builder().statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .message(String.format("%s, 페이지를 새로고침 후 다시 시도해주세요.", BlogUtil.getErrorMessage(exception)))
                        .build());
    }

    @ExceptionHandler(TemplateEngineException.class)
    public String templateEngineExceptionHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("error", BlogUtil.getErrorMessage(exception));
        log.error("[freeblog-fileSizeLimitExceededHandler] TemplateEngineException occurred ", exception);
        return "error/error-page";
    }

    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("error", BlogUtil.getErrorMessage(exception));
        log.error("[freeblog-exceptionHandler] exception occurred ", exception);
        return "error/error-page";
    }
}