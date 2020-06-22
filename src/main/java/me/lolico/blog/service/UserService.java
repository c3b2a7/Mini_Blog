package me.lolico.blog.service;

import me.lolico.blog.service.entity.User;

public interface UserService {
    User findUserByName(String name);

    User findUserForLogin(String loginName);

    boolean isExist(String name, String email, String phone);

    User registerAnAccount(User user);

    void updateForLogin(User user);

    void logout(User user);

    String generateMailConfirmationCode(String email);

    boolean confirm(String code);

    boolean isLocked(User user);

    boolean isDisable(User user);

    String getSalt(User user);

    void delete(int id);
}
