package com.license.service;

import com.license.entityModel.Permission;
import com.license.entityModel.Role;
import com.license.repository.PermissionRepository;
import com.license.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public Role createRole(String roleName, Set<String> permissionNames) {
        Set<Permission> permissions = permissionNames.stream()
                .map(permissionName -> permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permissionName)))
                .collect(Collectors.toSet());

        Role role = new Role();
        role.setName(roleName);
        role.setPermissions(permissions);

        return roleRepository.save(role);
    }
}
