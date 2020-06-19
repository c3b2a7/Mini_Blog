package me.lolico.blog.service.repo;

import me.lolico.blog.service.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lolico
 */
@Repository
@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User, Long> {
    User findByName(String name);

    User findByEmail(String email);

    User findByPhone(String phone);

    User findByNameOrEmailOrPhone(String name, String email, String phone);

}
