package ru.mirea.coursework.defecttracker.adapter.persistence.entity;

import jakarta.persistence.*;
import ru.mirea.coursework.defecttracker.domain.UserRole;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class UserEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true, length = 64) private String username;
    @Column(name = "password_hash", nullable = false, length = 120) private String passwordHash;
    @Column(name = "display_name", nullable = false, length = 120) private String displayName;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private UserRole role;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected UserEntity() { }
    public UserEntity(UUID id, String username, String passwordHash, String displayName, UserRole role, Instant createdAt) {
        this.id = id; this.username = username; this.passwordHash = passwordHash;
        this.displayName = displayName; this.role = role; this.createdAt = createdAt;
    }
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public UserRole getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
}
