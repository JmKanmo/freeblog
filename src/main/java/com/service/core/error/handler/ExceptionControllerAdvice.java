package com.service.core.error.handler;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice(basePackages = "com.service")
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception exception, Model model, HttpServletResponse httpServletResponse) {
        String msg = exception.getMessage();
        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("error", (msg == null) ? exception.toString() : msg);
        return "error/error_page";
    }
}