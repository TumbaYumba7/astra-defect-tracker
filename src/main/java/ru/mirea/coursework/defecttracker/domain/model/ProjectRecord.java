package ru.mirea.coursework.defecttracker.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ProjectRecord(UUID id, String projectKey, String name, String gameTitle,
                            String description, Instant createdAt) { }
