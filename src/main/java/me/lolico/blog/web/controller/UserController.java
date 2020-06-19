package me.lolico.blog.web.controller;

import me.lolico.blog.service.MailService;
import me.lolico.blog.service.UserService;
import me.lolico.blog.service.entity.User;
import me.lolico.blog.web.Constants;
import me.lolico.blog.web.vo.ApiResult;
import me.lolico.blog.web.vo.UserVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author lolico
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

    @PostMapping
    public ApiResult register(@RequestBody UserVO userVO, HttpServletRequest request) throws FileNotFoundException, MessagingException, UnknownHostException {
        User user = userVO.castEntity();
        Optional<User> optionalUser = Optional.ofNullable(userService.registerAnAccount(user));
        if (optionalUser.isPresent()) {
            String code = userService.generateMailConfirmationCode(user.getEmail());
            String url = prepareConfirmationUrl(request.getLocalPort(), code);
            String text = "<h3>验证你的邮箱</h3>" +
                    "<p>前往链接完成验证：</p>" +
                    "<p><a href=\"http://%s\">http://%s</a></p>";
            mailService.sendHtmlMessage(user.getEmail(), "邮箱验证", text, url, url);
            return ApiResult.ok("注册成功");
        }
        return ApiResult.ok("用户已存在");
    }

    private String prepareConfirmationUrl(int port, String code) throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        if (port != 80) {
            return ip + ":" + port + Constants.MAIL_CONFIRMATION_SCHEME + code;
        }
        return ip + Constants.MAIL_CONFIRMATION_SCHEME + code;
    }
}
