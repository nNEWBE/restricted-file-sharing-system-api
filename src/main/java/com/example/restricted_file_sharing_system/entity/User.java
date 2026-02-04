package com.example.restricted_file_sharing_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(unique = true)
    private String verificationToken;

    private LocalDateTime verificationTokenExpiry;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SharedFile> uploadedFiles = new HashSet<>();

    @ManyToMany(mappedBy = "sharedWith", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<SharedFile> accessibleFiles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isVerificationTokenValid() {
        return verificationToken != null &&
                verificationTokenExpiry != null &&
                LocalDateTime.now().isBefore(verificationTokenExpiry);
    }
}
