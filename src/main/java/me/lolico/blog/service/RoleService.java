package me.lolico.blog.service;

import me.lolico.blog.service.entity.Role;

/**
 * @author Lolico Li
 */
public interface RoleService {

    Role addRole(String name);

    Role findRole(String name);

    void deleteRole(Long id);

    Role createRole(String name, boolean persist);

}
