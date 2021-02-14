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
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * @author lolico
 */
@RequestMapping("account")
@RestController
public class AccountController {

    private final UserService userService;
    private final MailService mailService;

    public AccountController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @PostMapping("/register")
    public ApiResult register(@RequestBody @Valid UserVO userVO, HttpServletRequest request) throws FileNotFoundException, MessagingException, UnknownHostException {
        User user = userVO.castEntity();
        Optional<User> optionalUser = Optional.ofNullable(userService.registerAnAccount(user));
        if (optionalUser.isPresent()) {
            String code = userService.generateMailConfirmationCode(user.getEmail());
            String url = prepareConfirmationUrl(request.getLocalPort(), code);
            mailService.sendHtmlMessage(user.getEmail(), "邮箱验证", Constants.MAIL_CONFIRMATION_TEXT, url, url);
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
