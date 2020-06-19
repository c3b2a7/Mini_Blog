package me.lolico.blog.web.handler;

import me.lolico.blog.web.vo.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author lolico
 */
@RestControllerAdvice
public class ControllerAdviceHandler {

    @ExceptionHandler(Exception.class)
    public ApiResult handler(Exception ex) {
        return ApiResult.status(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getClass().getSimpleName());
    }

}
