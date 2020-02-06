package me.lolicom.blog.web.handler;

import me.lolicom.blog.web.vo.AjaxResponse;
import org.apache.shiro.authc.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author lolicom
 */
@RestControllerAdvice
public class ControllerAdviceHandler {
    
    @ExceptionHandler(AuthenticationException.class)
    public AjaxResponse handler(AuthenticationException ex) {
        if (ex instanceof UnknownAccountException) {
            return AjaxResponse.fail("账户不存在");
        } else if (ex instanceof LockedAccountException) {
            return AjaxResponse.fail("账户被锁定");
        } else if (ex instanceof IncorrectCredentialsException) {
            return AjaxResponse.fail("令牌错误");
        } else if (ex instanceof ExpiredCredentialsException) {
            return AjaxResponse.fail("令牌过期");
        } else if (ex instanceof ExcessiveAttemptsException) {
            return AjaxResponse.fail("操作频繁");
        } else if (ex instanceof DisabledAccountException) {
            return AjaxResponse.fail("账户不可用");
        } else {
            return AjaxResponse.fail("未知的认证错误：%s", ex.getMessage());
        }
    }
    
}
