package me.lolicom.blog.web.handler;

import me.lolicom.blog.web.vo.ApiResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author lolicom
 */
@RestControllerAdvice
public class ControllerAdviceHandler {

    @ExceptionHandler(Exception.class)
    public ApiResult handler(Exception ex) {
        return null;
    }

}
