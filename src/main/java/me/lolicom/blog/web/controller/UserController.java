package me.lolicom.blog.web.controller;

import me.lolicom.blog.service.MailService;
import me.lolicom.blog.service.UserService;
import me.lolicom.blog.service.entity.User;
import me.lolicom.blog.web.vo.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * @author lolicom
 */
@RequestMapping("user")
@RestController
public class UserController {

    private final UserService userService;
    private final MailService mailService;

    public UserController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping
    public ApiResult register(String username, String password, String email) throws FileNotFoundException, MessagingException {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);
        Optional<User> optionalUser = Optional.ofNullable(userService.registerAnAccount(user));
        if (optionalUser.isPresent()) {
            String url = userService.createConfirmationUrl(user);
            String text = "<h3>验证你的邮箱</h3>" +
                    "<p>前往链接完成验证：</p>" +
                    "<p><a href=\"http://%s\">http://%s</a></p>";
            mailService.sendHtmlMessage(email, "邮箱验证", text, url, url);
            return ApiResult.ok("注册成功");
        }
        return ApiResult.ok("用户已存在");
    }

}
