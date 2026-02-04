package com.example.restricted_file_sharing_system.repository;

import com.example.restricted_file_sharing_system.entity.SharedFile;
import com.example.restricted_file_sharing_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedFileRepository extends JpaRepository<SharedFile, Long> {

    Optional<SharedFile> findByDownloadToken(String downloadToken);

    Optional<SharedFile> findByStoredFileName(String storedFileName);

    boolean existsByDownloadToken(String downloadToken);

    List<SharedFile> findByOwner(User owner);

    List<SharedFile> findByOwnerId(Long ownerId);

    @Query("SELECT f FROM SharedFile f JOIN f.sharedWith u WHERE u.id = :userId")
    List<SharedFile> findFilesSharedWithUser(@Param("userId") Long userId);

    @Query("SELECT DISTINCT f FROM SharedFile f LEFT JOIN f.sharedWith u " +
            "WHERE f.owner.id = :userId OR u.id = :userId")
    List<SharedFile> findAllAccessibleByUser(@Param("userId") Long userId);
}
