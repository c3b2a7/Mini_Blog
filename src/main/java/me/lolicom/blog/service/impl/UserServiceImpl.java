package me.lolicom.blog.service.impl;

import me.lolicom.blog.entity.User;
import me.lolicom.blog.repository.UserRepository;
import me.lolicom.blog.service.UserService;
import me.lolicom.blog.util.HashUtils;
import me.lolicom.blog.util.SystemInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lolicom
 */
@Service
public class UserServiceImpl implements UserService {
    private final SystemInfo systemInfo;
    private final UserRepository repository;
    
    public UserServiceImpl(UserRepository repository, SystemInfo systemInfo) {
        this.repository = repository;
        this.systemInfo = systemInfo;
    }
    
    @Override
    public User findUserByName(String name) {
        return repository.findByName(name);
    }
    
    @Override
    public User findUserForLogin(String loginName) {
        User user;
        if (loginName.matches("^(\\w|-)+@(\\w|-)+(\\.(\\w|-)+)+$")) {
            user = repository.findByEmail(loginName);
        } else if (loginName.matches("^1[3-9]\\d{9}$")) {
            user = repository.findByPhone(loginName);
        } else {
            user = repository.findByName(loginName);
        }
        return user;
    }
    
    @Override
    public boolean isExist(String name, String email, String phone) {
        return Optional.ofNullable(repository.findByNameOrEmailOrPhone(name, email, phone))
                .isPresent();
    }
    
    @Override
    @Transactional
    public User registerAnAccount(User user) {
        if (isExist(user.getName(), user.getEmail(), user.getPhone())) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        
        user.setRegistrationIp(request.getRemoteAddr());
        user.setRegistrationTime(Timestamp.from(Instant.now()));
        // user.setRegistrationTime(new Timestamp(Instant.now().getEpochSecond() * 1000));
        user.setStatus(User.Status.WAITING_CONFIRMATION);
        user.setPassword(HashUtils.hash(user.getPassword(), getSalt(user)));
        
        return repository.save(user);
    }
    
    @Override
    public void updateForLogin(User user) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String remoteAddr = request.getRemoteAddr();
        user.setLastLoginIp(remoteAddr);
        user.setLastLoginTime(Timestamp.from(Instant.now()));
        repository.save(user);
    }
    
    @Override
    public void logout(User user) {
        user.setLastLogoutTime(Timestamp.from(Instant.now()));
        repository.save(user);
    }
    
    @Override
    public String getConfirmationUrl(User user) {
        String src = user.getName() + '.' + HashUtils.hash(user.getName());
        
        return systemInfo.getServerAddress() +
                "/u/confirm/" +
                Base64Utils.encodeToUrlSafeString(src.getBytes());
    }
    
    @Override
    public boolean confirm(String code) {
        byte[] bytes = Base64Utils.decodeFromUrlSafeString(code);
        String[] src = new String(bytes).split("\\.", 2);
        String name = src[0], hash = src[1];
        boolean result = hash.equals(HashUtils.hash(name));
        if (result) {
            User user = findUserByName(name);
            if (user.getStatus() != User.Status.VALID) {
                user.setStatus(User.Status.VALID);
                // repository.dynamicUpdate(user);
                repository.save(user);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isLocked(User user) {
        return user.getStatus() == User.Status.LOCKING;
    }
    
    @Override
    public boolean isDisable(User user) {
        return user.getStatus() == User.Status.WAITING_CONFIRMATION;
    }
    
    @Override
    public String getSalt(User user) {
        int nanos = user.getRegistrationTime().getNanos();
        if (nanos > 500000000) {
            return String.valueOf(user.getRegistrationTime().getTime() / 1000L + 1);
        }
        return String.valueOf(user.getRegistrationTime().getTime() / 1000L);
    }
}
