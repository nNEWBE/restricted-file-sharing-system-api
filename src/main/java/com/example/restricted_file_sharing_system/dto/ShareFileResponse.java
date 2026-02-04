package com.example.restricted_file_sharing_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareFileResponse {
    private Long fileId;
    private String fileName;
    private String downloadLink;
    private List<String> sharedWith;
    private List<String> notFoundEmails;
    private List<String> unverifiedEmails;
    private String message;
}
