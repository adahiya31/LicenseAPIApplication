package com.license.service;

import com.license.DTOs.LicenseRequest;
import com.license.exception.LicenseAlreadyExistsException;
import com.license.repository.LicenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "licensesEligibility", key = "#contentId", unless = "#result == false")
    public boolean isUserEligibleForLicense(String userId, String contentId) {
        if (userId == null || userId.trim().isEmpty() || contentId == null || contentId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID and Content ID must be provided");
        }

        Optional<LicenseRequest> licenseOpt = licenseRepository.findByContentId(contentId);

        logger.info("Checking license eligibility for contentId: {} and userId: {}", contentId, userId);

        if (licenseOpt.isPresent()) {
            LicenseRequest license = licenseOpt.get();
            return license.getUserId().equals(userId) && license.getExpiryAt().isAfter(LocalDateTime.now());
        }

        return false;
    }



    @Cacheable(value = "licenses", key = "#contentId")
    public List<LicenseRequest> getAllLicenses() {
        return licenseRepository.findAll();
    }

    public LicenseRequest getLicense(String contentId) {
        return licenseRepository.findByContentId(contentId)
                .orElse(null);
    }

    @CachePut(value = "licenses", key = "#license.contentId")
    @Transactional
    public LicenseRequest createLicense(String contentId, String userId) {
        // Validate input
        if (contentId == null || contentId.trim().isEmpty() || userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Content ID and User ID must be provided");
        }

        // Check if a license already exists
        Optional<LicenseRequest> existingLicense = licenseRepository.findByContentId(contentId);
        if (existingLicense.isPresent()) {
            throw new LicenseAlreadyExistsException("License already exists for contentId: " + contentId);
        }

        // Create and save the new license
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
        // Check if the license exists
        Optional<LicenseRequest> existingLicenseOpt = licenseRepository.findByContentId(license.getContentId());
        if (!existingLicenseOpt.isPresent()) {
            throw new IllegalArgumentException("License not found for contentId: " + license.getContentId());
        }

        // Update and save the license
        license.setCreatedAt(existingLicenseOpt.get().getCreatedAt());  // Retain the creation time
        return licenseRepository.save(license);
    }

   @Transactional
   @CacheEvict(value = "licenses", key = "#contentId")
   public void deleteLicense(String contentId) {
        // Check if the license exists
        Optional<LicenseRequest> existingLicenseOpt = licenseRepository.findByContentId(contentId);
        if (!existingLicenseOpt.isPresent()) {
            throw new IllegalArgumentException("License not found for contentId: " + contentId);
        }

        // Delete the license from the repository
        licenseRepository.deleteByContentId(contentId);
    }
}