package me.lolico.blog.web.controller;

import me.lolico.blog.lang.LimitLevel;
import me.lolico.blog.lang.annotation.CheckParam;
import me.lolico.blog.lang.annotation.Limit;
import me.lolico.blog.service.MailService;
import me.lolico.blog.service.UserService;
import me.lolico.blog.util.CaptchaGenerator;
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
import java.util.concurrent.TimeUnit;

/**
 * @author lolico
 */
@RequestMapping("public")
@RestController
public class PublicController {
    private final UserService userService;

    public PublicController(UserService userService, MailService mailService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
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

    @Limit(maxLimit = 1, limitLevel = LimitLevel.User, timeUnit = TimeUnit.MINUTES)
    @GetMapping(value = "/img", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage getImage() {
        return CaptchaGenerator.getInstance().generateImage();
    }

    @Limit(maxLimit = 1, limitLevel = LimitLevel.User, timeUnit = TimeUnit.MINUTES)
    @GetMapping(value = "/img2", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage2() throws IOException {
        BufferedImage bufferedImage = CaptchaGenerator.getInstance().generateImage();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpeg", outputStream);
        return outputStream.toByteArray();
    }

}
