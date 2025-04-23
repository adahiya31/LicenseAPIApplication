package com.license.controller;

import com.license.DTOs.CreateRoleRequest;
import com.license.entityModel.Role;
import com.license.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody CreateRoleRequest request) {
        Role role = roleService.createRole(
                request.getName(),
                request.getPermissionNames()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }
}
