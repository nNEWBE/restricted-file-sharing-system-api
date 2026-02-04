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
@Table(name = "shared_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true)
    private String storedFileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false, unique = true)
    private String downloadToken;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "file_shares", joinColumns = @JoinColumn(name = "file_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private Set<User> sharedWith = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public boolean hasAccess(User user) {
        if (user == null) {
            return false;
        }
        if (owner != null && owner.getId().equals(user.getId())) {
            return true;
        }
        return sharedWith.stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
    }

    public void shareWith(User user) {
        if (user != null && !user.getId().equals(owner.getId())) {
            sharedWith.add(user);
        }
    }

    public void revokeAccess(User user) {
        if (user != null) {
            sharedWith.removeIf(u -> u.getId().equals(user.getId()));
        }
    }
}
