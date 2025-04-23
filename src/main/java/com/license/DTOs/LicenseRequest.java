package com.license.DTOs;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table
@Data
public class LicenseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Content ID cannot be null")
    @Size(min = 1, max = 10, message = "Content ID must be between 1 and 10 characters")
    @Column(nullable = false)
    private String contentId;

    @NotNull(message = "User ID cannot be null")
    @Size(min = 1, max = 10, message = "User ID must be between 1 and 10 characters")
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiryAt;
}
