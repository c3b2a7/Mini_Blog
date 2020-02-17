package me.lolicom.blog.web.controller;

import me.lolicom.blog.service.UserService;
import me.lolicom.blog.web.vo.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lolicom
 */
@RequestMapping("api")
@RestController
public class ApiCommonController {
    private final UserService userService;

    public ApiCommonController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/confirm/{code}")
    public ApiResult confirm(@PathVariable("code") String code) {
        if (userService.confirm(code)) {
            return ApiResult.ok("验证成功", null);
        } else {
            return ApiResult.ok("验证失败", null);
        }
    }

    @GetMapping("/{msg}")
    public ApiResult hello(@PathVariable("msg") String msg) {
        return ApiResult.ok(msg);
    }

}
