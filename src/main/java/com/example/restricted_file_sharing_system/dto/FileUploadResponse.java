package com.example.restricted_file_sharing_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for file upload operation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {
    private Long fileId;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private String downloadLink;
    private LocalDateTime uploadedAt;
    private String message;
}
