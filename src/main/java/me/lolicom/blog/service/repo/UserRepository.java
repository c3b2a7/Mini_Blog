package me.lolicom.blog.service.repo;

import me.lolicom.blog.service.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lolicom
 */
@Repository
@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User, Long> {
    User findByName(String name);
    
    User findByEmail(String email);
    
    User findByPhone(String phone);
    
    Boolean findByNameOrEmailOrPhone(String name, String email, String phone);
    
}
