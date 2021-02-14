package me.lolico.blog.web.vo;

import lombok.Data;
import me.lolico.blog.service.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author lolico
 */
@Data
public class UserVO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Email
    private String email;
    @NotBlank
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
