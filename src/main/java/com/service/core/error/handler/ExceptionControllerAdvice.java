package com.service.core.error.handler;

import com.service.core.error.model.UserManageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @ExceptionHandler(value = UserManageException.class)
    @ResponseBody
    public ResponseEntity<String> userManageExceptionHandler(UserManageException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}