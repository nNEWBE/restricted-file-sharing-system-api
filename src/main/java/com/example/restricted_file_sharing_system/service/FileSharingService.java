package com.example.restricted_file_sharing_system.service;

import com.example.restricted_file_sharing_system.dto.FileInfoResponse;
import com.example.restricted_file_sharing_system.dto.FileUploadResponse;
import com.example.restricted_file_sharing_system.dto.ShareFileResponse;
import com.example.restricted_file_sharing_system.entity.SharedFile;
import com.example.restricted_file_sharing_system.entity.User;
import com.example.restricted_file_sharing_system.exception.AccessDeniedException;
import com.example.restricted_file_sharing_system.exception.FileNotFoundException;
import com.example.restricted_file_sharing_system.exception.UserNotFoundException;
import com.example.restricted_file_sharing_system.repository.SharedFileRepository;
import com.example.restricted_file_sharing_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileSharingService {

    private final SharedFileRepository sharedFileRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.verification.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, String userEmail) {
        User user = getUser(userEmail);

        String storedFileName = fileStorageService.storeFile(file);

        String downloadToken = UUID.randomUUID().toString();

        SharedFile sharedFile = SharedFile.builder()
                .originalFileName(file.getOriginalFilename())
                .storedFileName(storedFileName)
                .fileSize(file.getSize())
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .owner(user)
                .downloadToken(downloadToken)
                .build();

        sharedFile = sharedFileRepository.save(sharedFile);

        String downloadLink = buildDownloadLink(downloadToken);

        log.info("File uploaded by {}: {}", userEmail, sharedFile.getOriginalFileName());

        return FileUploadResponse.builder()
                .fileId(sharedFile.getId())
                .fileName(sharedFile.getOriginalFileName())
                .fileSize(sharedFile.getFileSize())
                .contentType(sharedFile.getContentType())
                .downloadLink(downloadLink)
                .uploadedAt(sharedFile.getUploadedAt())
                .message("File uploaded successfully")
                .build();
    }

    @Transactional
    public ShareFileResponse shareFile(Long fileId, List<String> userEmails, String ownerEmail) {
        User owner = getUser(ownerEmail);

        SharedFile sharedFile = sharedFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));

        if (!sharedFile.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You are not the owner of this file");
        }

        List<String> successEmails = new ArrayList<>();
        List<String> notFoundEmails = new ArrayList<>();
        List<String> unverifiedEmails = new ArrayList<>();

        String downloadLink = buildDownloadLink(sharedFile.getDownloadToken());

        for (String email : userEmails) {
            if (email.equalsIgnoreCase(ownerEmail)) {
                continue;
            }

            Optional<User> recipientOpt = userRepository.findByEmail(email);

            if (recipientOpt.isEmpty()) {
                notFoundEmails.add(email);
                continue;
            }

            User recipient = recipientOpt.get();

            if (!recipient.getEmailVerified()) {
                unverifiedEmails.add(email);
                continue;
            }

            if (!sharedFile.hasAccess(recipient)) {
                sharedFile.shareWith(recipient);
                successEmails.add(email);

                emailService.sendFileSharedNotification(
                        email,
                        ownerEmail,
                        sharedFile.getOriginalFileName(),
                        downloadLink);
            }
        }

        sharedFileRepository.save(sharedFile);

        log.info("File {} shared with {} users by {}", sharedFile.getOriginalFileName(), successEmails.size(),
                ownerEmail);

        return ShareFileResponse.builder()
                .fileId(sharedFile.getId())
                .fileName(sharedFile.getOriginalFileName())
                .downloadLink(downloadLink)
                .sharedWith(successEmails)
                .notFoundEmails(notFoundEmails)
                .unverifiedEmails(unverifiedEmails)
                .message("File shared successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public FileDownloadResult downloadFile(String downloadToken, String userEmail) {
        SharedFile sharedFile = sharedFileRepository.findByDownloadToken(downloadToken)
                .orElseThrow(() -> new FileNotFoundException("File not found or invalid token"));

        User user = getUser(userEmail);

        if (!sharedFile.hasAccess(user)) {
            log.warn("Access denied for user {} to file {}", userEmail, sharedFile.getOriginalFileName());
            throw new AccessDeniedException("You do not have permission to access this file");
        }

        Resource resource = fileStorageService.loadFileAsResource(sharedFile.getStoredFileName());

        log.info("File downloaded by {}: {}", userEmail, sharedFile.getOriginalFileName());

        return new FileDownloadResult(
                resource,
                sharedFile.getOriginalFileName(),
                sharedFile.getContentType(),
                sharedFile.getFileSize());
    }

    @Transactional(readOnly = true)
    public List<FileInfoResponse> getAccessibleFiles(String userEmail) {
        User user = getUser(userEmail);

        List<SharedFile> files = sharedFileRepository.findAllAccessibleByUser(user.getId());

        return files.stream().map(file -> FileInfoResponse.builder()
                .fileId(file.getId())
                .fileName(file.getOriginalFileName())
                .fileSize(file.getFileSize())
                .contentType(file.getContentType())
                .downloadLink(buildDownloadLink(file.getDownloadToken()))
                .ownerEmail(file.getOwner().getEmail())
                .uploadedAt(file.getUploadedAt())
                .sharedWith(file.getSharedWith().stream().map(User::getEmail).collect(Collectors.toList()))
                .build())
                .collect(Collectors.toList());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    private String buildDownloadLink(String token) {
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(token)
                    .toUriString();
        } catch (Exception e) {
            return baseUrl + "/api/files/download/" + token;
        }
    }

    public record FileDownloadResult(
            Resource resource,
            String originalFileName,
            String contentType,
            Long fileSize) {
    }
}
