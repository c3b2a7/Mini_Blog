package me.lolicom.blog.service.impl;

import io.jsonwebtoken.Claims;
import me.lolicom.blog.lang.SystemInfo;
import me.lolicom.blog.service.UserService;
import me.lolicom.blog.service.entity.User;
import me.lolicom.blog.service.repo.UserRepository;
import me.lolicom.blog.util.JwtUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author lolicom
 */
@Service
public class UserServiceImpl implements UserService {
    private final SystemInfo systemInfo;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserServiceImpl(UserRepository repository, SystemInfo systemInfo) {
        this.repository = repository;
        this.systemInfo = systemInfo;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
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
        user.setRegistrationTime(new Timestamp(Instant.now().getEpochSecond() * 1000));
        user.setStatus(User.Status.WAITING_CONFIRMATION);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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
    public String createConfirmationUrl(User user) {
        String src = JwtUtils.createToken(user.getName(), new HashMap<>(), 12, TimeUnit.HOURS);
        return systemInfo.getServerAddress() +
                "/api/confirm/" + src;
    }

    @Override
    public boolean confirm(String code) {
        try {
            JwtUtils.verifyToken(code);
            String subject = JwtUtils.getProperty(code, Claims::getSubject);
            if (subject != null) {
                User user = findUserForLogin(subject);
                if (user.getStatus() != User.Status.VALID) {
                    user.setStatus(User.Status.VALID);
                    repository.save(user);
                }
                return true;
            }
        } catch (Exception ignored) {
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
