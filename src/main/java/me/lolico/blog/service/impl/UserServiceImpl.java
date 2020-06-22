package me.lolico.blog.service.impl;

import me.lolico.blog.service.RoleService;
import me.lolico.blog.service.UserService;
import me.lolico.blog.service.entity.Role;
import me.lolico.blog.service.entity.User;
import me.lolico.blog.service.repo.UserRepository;
import me.lolico.blog.util.RequestUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lolico
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserServiceImpl(UserRepository repository, RoleService roleService) {
        this.repository = repository;
        this.roleService = roleService;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Override
    // @DataSource("dataSource1")
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

        user.setRegistrationIp(RequestUtils.getIp(request));
        user.setRegistrationTime(Timestamp.from(Instant.now()));
        user.setRegistrationTime(new Timestamp(Instant.now().getEpochSecond() * 1000));
        user.setStatus(User.Status.VALID);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role visitor = roleService.createRole("VISITOR", true);
        user.setRole(visitor);

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
    public String generateMailConfirmationCode(String email) {
        String digest = DigestUtils.md5DigestAsHex(email.getBytes());
        return Base64Utils.encodeToUrlSafeString((email.replace(".", "$") + "." + digest).getBytes());
    }

    @Override
    public boolean confirm(String code) {
        try {
            byte[] bytes = Base64Utils.decodeFromUrlSafeString(code);
            String[] src = new String(bytes).split("\\.");
            src[0] = src[0].replace("$", ".");
            if (DigestUtils.md5DigestAsHex(src[0].getBytes()).equals(src[1])) {
                User user = repository.findByEmail(src[0]);
                if (user.getStatus() == User.Status.WAITING_CONFIRMATION) {
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

    @Override
    public void delete(int id) {
        if (id > 0) {
            repository.deleteById((long) id);
            return;
        }
        throw new IllegalArgumentException("id must be greater than zero");
    }

    public List<User> dynamicFind(User user) {
        return repository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (user.getId() > 0) {
                predicates.add(criteriaBuilder.equal(root.<Integer>get("id"), user.getId()));
            }
            if (!StringUtils.isEmpty(user.getName())) {
                predicates.add(criteriaBuilder.equal(root.<String>get("username"), user.getName()));
            }
            if (!StringUtils.isEmpty(user.getPassword())) {
                predicates.add(criteriaBuilder.equal(root.<String>get("password"), user.getPassword()));
            }
            if (!StringUtils.isEmpty(user.getEmail())) {
                predicates.add(criteriaBuilder.equal(root.<String>get("email"), user.getEmail()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
