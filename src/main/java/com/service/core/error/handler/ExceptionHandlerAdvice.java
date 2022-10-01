package com.service.core.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fileSizeLimitExceededHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        String msg = exception.getMessage();
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("error", (msg == null) ? exception.toString() : msg);
        log.error("[freeblog-fileSizeLimitExceededHandler] MaxUploadSizeExceededException occurred ", exception.toString());
        return "error/error-page";
    }

    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        String msg = exception.getMessage();
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("error", (msg == null) ? exception.toString() : msg);
        log.error("[freeblog-exceptionHandler] exception occurred ", exception.toString());
        return "error/error-page";
    }
}