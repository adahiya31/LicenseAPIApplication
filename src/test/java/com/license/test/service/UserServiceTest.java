package com.license.test.service;


import com.license.entityModel.Permission;
import com.license.entityModel.Role;
import com.license.repository.PermissionRepository;
import com.license.repository.RoleRepository;
import com.license.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleService roleService;

    private Permission permission;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        permission = new Permission(1L, "READ_PERMISSION");
        role = new Role();
        role.setId(1L);
        role.setName("Admin");
        role.setPermissions(Set.of(permission));
    }

    @Test
    void testCreateRole_Success() {
        when(permissionRepository.findByName("READ_PERMISSION")).thenReturn(Optional.of(permission));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.createRole("Admin", Set.of("READ_PERMISSION"));

        assertNotNull(result);
        assertEquals("Admin", result.getName());
        assertTrue(result.getPermissions().stream()
                .anyMatch(p -> p.getName().equals("READ_PERMISSION")));

        verify(permissionRepository, times(1)).findByName("READ_PERMISSION");
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testCreateRole_PermissionNotFound() {
        when(permissionRepository.findByName("INVALID_PERMISSION")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            roleService.createRole("Admin", Set.of("INVALID_PERMISSION"));
        });

        assertEquals("Permission not found: INVALID_PERMISSION", exception.getMessage());
        verify(permissionRepository, times(1)).findByName("INVALID_PERMISSION");
        verify(roleRepository, never()).save(any(Role.class));
    }
}