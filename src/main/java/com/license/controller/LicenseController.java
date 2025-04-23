package com.license.controller;

import com.license.DTOs.LicenseRequest;
import com.license.DTOs.LicenseResponse;
import com.license.exception.ErrorResponse;
import com.license.service.LicenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@Tag(name = "License API", description = "APIs to manage licenses")

public class LicenseController {

    private final LicenseService licenseService;

    @Autowired
    public LicenseController(LicenseService licenseService) {

        this.licenseService = licenseService;
    }

    @Operation(summary = "Get all licenses", description = "Retrieve a list of all licenses")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved licenses")
    @GetMapping("/all/license")
    public ResponseEntity<List<LicenseRequest>> getAllLicenses() {
        List<LicenseRequest> licenses = licenseService.getAllLicenses();
        return ResponseEntity.status(HttpStatus.OK).body(licenses);
    }

    @Operation(summary = "Check license eligibility", description = "Check if the authenticated user is eligible for a license based on the contentId")
    @ApiResponse(responseCode = "200", description = "Eligibility status of the user")
    @ApiResponse(responseCode = "400", description = "Bad Request if the contentId is missing or invalid")
    @GetMapping("/license")
    public ResponseEntity<LicenseResponse> checkLicenseEligibility(@Valid @RequestParam String contentId) {
        String userId = getAuthenticatedUserId();
        boolean isEligible = licenseService.isUserEligibleForLicense(userId, contentId);
        return ResponseEntity.status(HttpStatus.OK).body(new LicenseResponse(isEligible));
    }



    @Operation(summary = "Get license details", description = "Retrieve license details by contentId")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved license details")
    @ApiResponse(responseCode = "404", description = "License not found for the given contentId")
    @GetMapping("/license/details")
    public ResponseEntity<?> getLicense(@Valid @RequestParam String contentId) {
        LicenseRequest license = licenseService.getLicense(contentId);
        if (license != null) {
            return ResponseEntity.ok(license);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponse("License Not Found", "No license for contentId: " + contentId, LocalDateTime.now())
            );
        }
    }

    @Operation(summary = "Create a new license", description = "Create a new license for a specific contentId and userId")
    @ApiResponse(responseCode = "201", description = "License successfully created")
    @ApiResponse(responseCode = "400", description = "Bad Request if the input is invalid")
    @ApiResponse(responseCode = "409", description = "Conflict if the license already exists")
    @PostMapping("/license")
    public ResponseEntity<?> createLicense(@Valid @RequestParam String contentId,
                                           @Valid @RequestParam String userId) {
        LicenseRequest license = licenseService.createLicense(contentId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(license);

    }

    @Operation(summary = "Update an existing license", description = "Update an existing license")
    @ApiResponse(responseCode = "200", description = "License updated successfully")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PutMapping("/license")
    public ResponseEntity<?> updateLicense(@Valid @RequestBody LicenseRequest licenseRequest) {
        LicenseRequest updatedLicense = licenseService.updateLicense(licenseRequest);
        return ResponseEntity.status(HttpStatus.OK).body(updatedLicense);
    }

    @Operation(summary = "Delete a license", description = "Delete a license by contentId")
    @ApiResponse(responseCode = "200", description = "License deleted successfully")
    @ApiResponse(responseCode = "404", description = "License not found for the given contentId")
    @DeleteMapping("/license")
    public ResponseEntity<?> deleteLicense(@RequestParam String contentId) {
        licenseService.deleteLicense(contentId);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse("Success",
                "License deleted successfully", LocalDateTime.now()));
    }

    private String getAuthenticatedUserId() {

        return "authenticatedUserId";
    }

}