package com.rose.services;

import com.rose.entities.enums.ERole;
import com.rose.entities.Role;

import java.util.Optional;
import java.util.Set;

public interface IRoleService {
    Optional<Role> findByName(ERole roleUser);

    Role getDefaultRole();

    Set<Role> getRolesBySetRoles(Set<ERole> stringSet);
}
