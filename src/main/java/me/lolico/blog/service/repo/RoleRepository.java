package me.lolico.blog.service.repo;

import me.lolico.blog.service.entity.Role;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lolico Li
 */
@Repository
@Transactional(readOnly = true)
public interface RoleRepository extends BaseRepository<Role, Long> {

    Role findByName(String name);

}
