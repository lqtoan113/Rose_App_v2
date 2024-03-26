package com.rose.services.impl;

import com.rose.entities.enums.ERole;
import com.rose.entities.Role;
import com.rose.repositories.RoleRepository;
import com.rose.services.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired private RoleRepository roleRepository;

    /**
     * @param roleUser
     * @return Role
     */
    @Override
    public Optional<Role> findByName(ERole roleUser) {
        return roleRepository.findByName(roleUser);
    }

    /**
     * @return Role
     */
    @Override
    public Role getDefaultRole() {
        return roleRepository.findByName(ERole.ROLE_USER).get();
    }

    /**
     * @param stringSet
     * @return
     */
    @Override
    public Set<Role> getRolesBySetRoles(Set<ERole> stringSet) {
        Set<Role> roles = new HashSet<>();
        if (stringSet == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            stringSet.forEach(role -> {
                switch (role) {
                    case ROLE_ADMIN:
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case ROLE_MODERATOR:
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        return roles;
    }
}
