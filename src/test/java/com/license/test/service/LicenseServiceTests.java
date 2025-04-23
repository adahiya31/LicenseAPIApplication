package com.license.test.service;

import com.license.DTOs.LicenseRequest;
import com.license.exception.LicenseAlreadyExistsException;
import com.license.repository.LicenseRepository;
import com.license.service.LicenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class LicenseServiceTests {

    @InjectMocks
    private LicenseService licenseService;

    @Mock
    private LicenseRepository licenseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCreateLicense() {
        String contentId = "content123";
        String userId = "123";

        LicenseRequest license = new LicenseRequest();
        license.setContentId(contentId);
        license.setUserId(userId);
        license.setCreatedAt(LocalDateTime.now());

        when(licenseRepository.findByContentId(contentId)).thenReturn(Optional.empty());

        when(licenseRepository.save(any(LicenseRequest.class))).thenReturn(license);

        LicenseRequest createdLicense = licenseService.createLicense(contentId, userId);

        assertNotNull(createdLicense);
        assertEquals(contentId, createdLicense.getContentId());
        assertEquals(userId, createdLicense.getUserId());

        verify(licenseRepository).findByContentId(contentId);
        verify(licenseRepository).save(any(LicenseRequest.class));
    }

    @Test
    void testCreateLicense_AlreadyExists() {
        String contentId = "content123";
        String userId = "1223";

        LicenseRequest existingLicense = new LicenseRequest();
        existingLicense.setContentId(contentId);
        existingLicense.setUserId(userId);
        existingLicense.setCreatedAt(LocalDateTime.now());

        when(licenseRepository.findByContentId(contentId)).thenReturn(Optional.of(existingLicense));

        LicenseAlreadyExistsException exception = assertThrows(LicenseAlreadyExistsException.class, () -> licenseService.createLicense(contentId, userId));

        assertEquals("License already exists for contentId: " + contentId, exception.getMessage());

        verify(licenseRepository).findByContentId(contentId);
    }

    @Test
    void testGetLicense() {
        String contentId = "content123";
        LocalDateTime fixedTime = LocalDateTime.of(2025, 4, 21, 15, 0, 0, 0);

        LicenseRequest license = new LicenseRequest();
        license.setContentId(contentId);
        license.setUserId("123");
        license.setCreatedAt(fixedTime);

        when(licenseRepository.findByContentId(contentId)).thenReturn(Optional.of(license));

        LicenseRequest foundLicense = licenseService.getLicense(contentId);

        assertNotNull(foundLicense);
        assertEquals(contentId, foundLicense.getContentId());
        assertEquals(fixedTime, foundLicense.getCreatedAt());
        verify(licenseRepository).findByContentId(contentId);
    }

    @Test
    void testIsUserEligibleForLicense() {
        String contentId = "content123";
        String userId = "123";

        LicenseRequest license = new LicenseRequest();
        license.setContentId(contentId);
        license.setUserId("userId");
        license.setCreatedAt(LocalDateTime.now());
        license.setExpiryAt(LocalDateTime.now().plusHours(10));

        when(licenseRepository.findByContentId(contentId)).thenReturn(Optional.of(license));

        boolean isEligible = licenseService.isUserEligibleForLicense(userId, contentId);

        assertTrue(isEligible);
    }

    @Test
    void testIsUserEligibleForLicense_Expired() {
        String contentId = "content123";
        String userId = "123L";

        LicenseRequest license = new LicenseRequest();
        license.setContentId(contentId);
        license.setUserId(userId);
        license.setCreatedAt(LocalDateTime.now());
        license.setExpiryAt(LocalDateTime.now().minusHours(10));
        when(licenseRepository.findByContentId(contentId)).thenReturn(Optional.of(license));

        boolean isEligible = licenseService.isUserEligibleForLicense(userId, contentId);

        assertFalse(isEligible);
    }


}
