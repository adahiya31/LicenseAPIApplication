package com.license.service;

import com.license.DTOs.LicenseRequest;
import com.license.entityModel.Permission;
import com.license.entityModel.Role;
import com.license.entityModel.Rule;
import com.license.entityModel.User;
import com.license.exception.LicenseAlreadyExistsException;
import com.license.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LicenseService {

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);


    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Cacheable(value = "licensesEligibility", key = "#contentId")
    public boolean isUserEligibleForLicense(String userId, String contentId) {
        if (userId == null || userId.trim().isEmpty()
                || contentId == null || contentId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID and Content ID must be provided");
        }

        User user = userService.getUserById(Long.parseLong(userId));

        Optional<LicenseRequest> licenseOpt = licenseRepository.findByContentId(contentId);

        logger.info("Checking license eligibility for contentId: {} and userId: {}", contentId, userId);

        if (licenseOpt.isPresent()) {
            LicenseRequest license = licenseOpt.get();
            return license.getUserId().equals(userId)
                    && license.getExpiryAt().isAfter(LocalDateTime.now());
        }

        List<String> requiredRoles = List.of("Admin", "Premium User");
        boolean hasRequiredRole = user.getRoles().stream()
                .map(Role::getName)
                .anyMatch(requiredRoles::contains);

        if (!hasRequiredRole) {
            return false;
        }

        String requiredPermission = "LICENSE_ACCESS";
        boolean hasRequiredPermission = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .anyMatch(permission -> permission.equals(requiredPermission));

        if (!hasRequiredPermission) {
            return false;
        }

        List<Rule> rules = ruleRepository.findByContentId(contentId);
        for (Rule rule : rules) {
            boolean matchesRole = user.getRoles().stream()
                    .map(Role::getName)
                    .anyMatch(role -> role.equals(rule.getRequiredRole()));

            boolean matchesPermission = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(Permission::getName)
                    .anyMatch(permission -> permission.equals(rule.getRequiredPermission()));

            if (!matchesRole || !matchesPermission) {
                return false;
            }
        }

        return false;
    }

    public List<LicenseRequest> getAllLicenses() {
        return licenseRepository.findAll();
    }

    public LicenseRequest getLicense(String contentId) {
        return licenseRepository.findByContentId(contentId).orElse(null);
    }

    @Transactional
    public LicenseRequest createLicense(String contentId, String userId) {
        if (contentId == null || contentId.trim().isEmpty() || userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Content ID and User ID must be provided");
        }

        Optional<LicenseRequest> existingLicense = licenseRepository.findByContentId(contentId);
        if (existingLicense.isPresent()) {
            throw new LicenseAlreadyExistsException("License already exists for contentId: " + contentId);
        }

        LicenseRequest license = new LicenseRequest();
        license.setContentId(contentId);
        license.setUserId(userId);
        license.setCreatedAt(LocalDateTime.now());
        license.setExpiryAt(LocalDateTime.now().plusDays(30));

        logger.info("Creating license for contentId: {} and userId: {}", contentId, userId);

        return licenseRepository.save(license);
    }

    @Transactional
    @CachePut(value = "licenses", key = "#license.contentId")
    public LicenseRequest updateLicense(LicenseRequest license) {
        Optional<LicenseRequest> existingLicenseOpt = licenseRepository.findByContentId(license.getContentId());
        if (!existingLicenseOpt.isPresent()) {
            throw new IllegalArgumentException("License not found for contentId: " + license.getContentId());
        }
        license.setCreatedAt(existingLicenseOpt.get().getCreatedAt());
        return licenseRepository.save(license);
    }

    @Transactional
    @CacheEvict(value = "licenses", key = "#contentId")
    public void deleteLicense(String contentId) {
        Optional<LicenseRequest> existingLicenseOpt = licenseRepository.findByContentId(contentId);
        if (!existingLicenseOpt.isPresent()) {
            throw new IllegalArgumentException("License not found for contentId: " + contentId);
        }

        licenseRepository.deleteByContentId(contentId);
    }
}