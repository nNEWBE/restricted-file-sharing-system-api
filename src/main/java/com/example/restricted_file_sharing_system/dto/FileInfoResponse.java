package com.example.restricted_file_sharing_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfoResponse {
    private Long fileId;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private String downloadLink;
    private String ownerEmail;
    private List<String> sharedWith;
    private LocalDateTime uploadedAt;
}
