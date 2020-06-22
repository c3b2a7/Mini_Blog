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

    public static final String PREFIX = "ROLE_";

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public Role addRole(String name) {
        return createRole(name, true);
    }

    @Override
    public Role findRole(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Role createRole(String name, boolean persist) {
        String roleName = generateName(name);
        Role existRole = findRole(roleName);
        if (existRole != null) {
            return existRole;
        }
        Role role = new Role();
        role.setName(roleName);
        if (persist) {
            return roleRepository.save(role);
        }
        return role;
    }

    private String generateName(String name) {
        return PREFIX + name;
    }
}
