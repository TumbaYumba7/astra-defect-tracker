package ru.mirea.coursework.defecttracker.domain.model;

import ru.mirea.coursework.defecttracker.domain.UserRole;
import java.time.Instant;
import java.util.UUID;

public record UserRecord(UUID id, String username, String passwordHash, String displayName,
                        UserRole role, Instant createdAt) { }
