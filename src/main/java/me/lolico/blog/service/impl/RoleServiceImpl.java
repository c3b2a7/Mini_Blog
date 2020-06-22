package me.lolico.blog.service.impl;

import me.lolico.blog.service.RoleService;
import me.lolico.blog.service.entity.Role;
import me.lolico.blog.service.repo.RoleRepository;
import org.springframework.stereotype.Service;

/**
 * @author Lolico Li
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role addRole(String name) {
        return createRole(ensureRoleName(name), true);
    }

    @Override
    public Role findRole(String name) {
        return roleRepository.findByName(ensureRoleName(name));
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Role createRole(String name, boolean persist) {
        name = ensureRoleName(name);
        Role existRole = findRole(name);
        if (existRole != null) {
            return existRole;
        }
        Role role = new Role();
        role.setName(name);
        if (persist) {
            return roleRepository.save(role);
        }
        return role;
    }

    private String ensureRoleName(String name) {
        if (!name.startsWith(Role.PREFIX)) {
            return Role.PREFIX + name;
        }
        return name;
    }
}
