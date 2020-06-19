package me.lolico.blog.web.controller;

import me.lolico.blog.lang.annotation.WebLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lolico li
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    @WebLog(name = "test")
    public String get(String msg) {
        return msg;
    }
}
