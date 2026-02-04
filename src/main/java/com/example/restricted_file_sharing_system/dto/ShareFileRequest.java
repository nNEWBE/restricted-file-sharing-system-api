package com.example.restricted_file_sharing_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for sharing a file with users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareFileRequest {
    @NotEmpty(message = "At least one email is required")
    private List<@Email(message = "Invalid email format") String> emails;
}
