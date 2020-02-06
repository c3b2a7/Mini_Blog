package me.lolicom.blog.web.controller;

import me.lolicom.blog.entity.User;
import me.lolicom.blog.service.MailService;
import me.lolicom.blog.service.UserService;
import me.lolicom.blog.util.JwtUtils;
import me.lolicom.blog.web.vo.AjaxResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * @author lolicom
 */
@RestController
public class UserController {
    
    final UserService userService;
    final MailService mailService;
    final JwtUtils jwtUtils;
    
    public UserController(UserService userService, MailService mailService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.mailService = mailService;
        this.jwtUtils = jwtUtils;
    }
    
    @PostMapping("/login")
    public AjaxResponse login(String username, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        //login
        subject.login(token);
        //update info
        User user = (User) subject.getPrincipal();
        userService.updateForLogin(user);
        String jwt = jwtUtils.createToken(username, (map) -> {
            map.put("admin", false);
            return map;
        });
        return AjaxResponse.ok(jwt);
    }
    
    @GetMapping("/logout")
    public AjaxResponse logout() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        userService.logout(user);
        return AjaxResponse.ok("登出成功");
    }
    
    @PostMapping("/register")
    public AjaxResponse register(String username, String password, String email) throws FileNotFoundException, MessagingException {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);
        Optional<User> optionalUser = Optional.ofNullable(userService.registerAnAccount(user));
        if (optionalUser.isPresent()) {
            String url = userService.getConfirmationUrl(user);
            String text = "<h3>验证你的邮箱</h3>" +
                    "<p>前往链接完成验证：</p>" +
                    "<p><a href=\"http://%s\">http://%s</a></p>";
            mailService.sendHtmlMessage(email, "邮箱验证", text, url, url);
            return AjaxResponse.ok("注册成功");
        }
        return AjaxResponse.fail("用户已存在");
    }
    
    @GetMapping("/u/confirm/{code}")
    public AjaxResponse confirm(@PathVariable("code") String code) {
        if (userService.confirm(code)) {
            return AjaxResponse.ok();
        } else {
            return AjaxResponse.fail();
        }
    }
    
    @GetMapping("/u/t/{var}")
    public String test(@PathVariable("var") String var) {
        return var;
    }
    
}
