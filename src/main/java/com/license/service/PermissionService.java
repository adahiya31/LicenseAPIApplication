package com.license.service;

import com.license.entityModel.Permission;
import com.license.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public Permission createPermission(String permissionName) {
        Permission permission = new Permission();
        permission.setName(permissionName);
        return permissionRepository.save(permission);
    }
}
