package ru.mirea.coursework.defecttracker.adapter.web.dto;

import ru.mirea.coursework.defecttracker.domain.model.UserRecord;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String username, String displayName, String role, Instant createdAt) {
    public static UserResponse from(UserRecord u) {
        return new UserResponse(u.id(), u.username(), u.displayName(), u.role().name(), u.createdAt());
    }
}
