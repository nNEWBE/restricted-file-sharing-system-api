package com.example.restricted_file_sharing_system.controller;

import com.example.restricted_file_sharing_system.dto.FileInfoResponse;
import com.example.restricted_file_sharing_system.dto.FileUploadResponse;
import com.example.restricted_file_sharing_system.dto.ShareFileRequest;
import com.example.restricted_file_sharing_system.dto.ShareFileResponse;
import com.example.restricted_file_sharing_system.service.FileSharingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileSharingService fileSharingService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            Principal principal) {

        FileUploadResponse response = fileSharingService.uploadFile(file, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fileId}/share")
    public ResponseEntity<ShareFileResponse> shareFile(
            @PathVariable Long fileId,
            @Valid @RequestBody ShareFileRequest request,
            Principal principal) {

        ShareFileResponse response = fileSharingService.shareFile(fileId, request.getEmails(), principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{token}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String token,
            Principal principal) {

        FileSharingService.FileDownloadResult result = fileSharingService.downloadFile(token, principal.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + result.originalFileName() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(result.fileSize()))
                .body(result.resource());
    }

    @GetMapping
    public ResponseEntity<List<FileInfoResponse>> listFiles(Principal principal) {
        List<FileInfoResponse> files = fileSharingService.getAccessibleFiles(principal.getName());
        return ResponseEntity.ok(files);
    }
}
