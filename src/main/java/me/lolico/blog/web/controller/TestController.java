package me.lolico.blog.web.controller;

import me.lolico.blog.lang.annotation.CheckParam;
import me.lolico.blog.lang.annotation.DistributedLock;
import me.lolico.blog.web.LogReporter;
import me.lolico.blog.web.vo.ApiResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lolico li
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final ApplicationEventPublisher eventPublisher;

    public TestController(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/getMsg")
    @DistributedLock(key = "#msg", timeout = 10)
    @PreAuthorize("hasAnyRole('VISITOR','USER')")
    public String getMsg(String msg) {
        return msg;
    }


    @GetMapping("/get")
    @CheckParam(index = 0)
    @DistributedLock(key = "#msg", timeout = 10)
    @PreAuthorize("hasRole('USER')")
    public ApiResult get(String msg) {
        eventPublisher.publishEvent(LogReporter.logEvent(this));
        Map<String, Object> map = new HashMap<>();
        map.put("answer", true);
        map.put("msg", msg);
        return ApiResult.ok(map);
    }
}
