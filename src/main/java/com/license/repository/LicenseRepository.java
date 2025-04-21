package com.license.repository;

import com.license.DTOs.LicenseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<LicenseRequest, Long> {

    Optional<LicenseRequest> findByContentId(String contentId);
    void deleteByContentId(String contentId);
}
