package com.example.restricted_file_sharing_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for successful authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * JWT access token
     */
    private String accessToken;

    /**
     * Token type (Bearer)
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * User's email
     */
    private String email;

    /**
     * Response message
     */
    private String message;
}
