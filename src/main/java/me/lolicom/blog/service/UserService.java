package me.lolicom.blog.service;

import me.lolicom.blog.entity.User;

public interface UserService {
    User findUserByName(String name);
    
    User findUserForLogin(String loginName);
    
    boolean isExist(String name, String email,String phone);
    
    User registerAnAccount(User user);
    
    void updateForLogin(User user);
    
    void logout(User user);
    
    String getConfirmationUrl(User user);
    
    boolean confirm(String code);
    
    boolean isLocked(User user);
    
    boolean isDisable(User user);
    
    String getSalt(User user);
}
