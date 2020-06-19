package me.lolico.blog.web.controller;

import me.lolico.blog.lang.annotation.CheckParam;
import me.lolico.blog.lang.annotation.DistributedLock;
import me.lolico.blog.lang.annotation.WebLog;
import me.lolico.blog.service.MailService;
import me.lolico.blog.service.UserService;
import me.lolico.blog.util.CaptchaGenerator;
import me.lolico.blog.web.LogReporter;
import me.lolico.blog.web.vo.ApiResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lolico
 */
@RequestMapping("pub")
@RestController
public class PublicController {
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public PublicController(UserService userService, MailService mailService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/confirm/{code}")
    @CheckParam(index = 0)
    public ApiResult confirm(@PathVariable("code") String code) {
        if (userService.confirm(code)) {
            return ApiResult.ok("验证成功", null);
        } else {
            return ApiResult.ok("验证失败", null);
        }
    }

    @GetMapping(value = "/img", produces = MediaType.IMAGE_JPEG_VALUE)
    @WebLog(name = "/pub/img")
    @DistributedLock(key = "#msg", timeout = 10)
    public BufferedImage getImage(String msg) throws InterruptedException {
        Thread.sleep(8000);
        return CaptchaGenerator.getInstance().generateImage();
    }

    @GetMapping(value = "/img2", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage2() throws IOException {
        BufferedImage bufferedImage = CaptchaGenerator.getInstance().generateImage();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", outputStream);
        return outputStream.toByteArray();
    }

    @GetMapping(value = "/que")
    @CheckParam(index = 0)
    @WebLog(name = "/pub/que")
    @DistributedLock(key = "#msg", timeout = 10)
    public ApiResult get(String msg) {
        eventPublisher.publishEvent(LogReporter.logEvent(this));
        Map<String, Object> map = getStringBooleanMap();
        map.put("msg", msg);
        return ApiResult.ok(map);
    }

    private Map<String, Object> getStringBooleanMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("answer", true);
        return map;
    }

}
