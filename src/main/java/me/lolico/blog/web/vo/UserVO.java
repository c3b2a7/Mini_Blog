package me.lolico.blog.web.vo;

import lombok.Data;
import me.lolico.blog.service.entity.User;

/**
 * @author lolico
 */
@Data
public class UserVO {
    private String username;
    private String password;
    private String email;
    private String phone;

    public User castEntity() {
        User user = new User();
        if (username != null) {
            user.setName(username);
        }
        if (password != null) {
            user.setPassword(password);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (password != null) {
            user.setPhone(phone);
        }
        return user;
    }
}
